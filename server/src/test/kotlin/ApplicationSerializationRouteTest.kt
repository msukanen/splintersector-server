import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import net.msukanen.splintersector_server.configureDatabases
import net.msukanen.splintersector_server.configureSerialization
import net.msukanen.splintersector_server.db.RoomRepository
import net.msukanen.splintersector_server.model.Room
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationSerializationRouteTest {
    @Test
    fun `room can be found with refId`() = testApplication {
        application {
            configureDatabases()
            configureSerialization(RoomRepository())
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

    }

    /**
     See that the server responds in a sane way to both correct and
     faulty requests.

     Rules:
       - Accept correct path AND refId.
       - 400 with correct path but BAD refId.
       - 404 everything else.
     */
    @Test
    fun `server access obeys rules`() = testApplication {
        application {
            configureDatabases()
            configureSerialization(RoomRepository())
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Correct path with existing refId
        //  - amount of whitespace is irrelevant as a proper number can be
        //    derived even if such exists.
        val correctRefs = listOf("2", "    2", "%202   ")
        correctRefs.forEach {
            val response = client.get("/room/r/2")
            assertEquals(HttpStatusCode.OK, response.status)
            val room = Json.decodeFromString<Room>(response.body())
            assertEquals(2, room.reference)
            assertEquals("2", room.name)
        }

        // Bad Req *wrong* refIds (but correct path)
        val correctPath = "/room/r/"
        val wrongRefs = listOf("-10x900", "foobar", "0x02", "x02", "\\00a0", "\\02")
        wrongRefs.forEach {
            val response = client.get("$correctPath$it")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

        // Ignore *wrong* paths
        var response = client.get("/room/x/y/z")
        assertEquals(HttpStatusCode.NotFound, response.status)

        // Ignore POST
        response = client.post("\\zyxel\\m/r/666a999")
        assertEquals(HttpStatusCode.NotFound, response.status)

        // Ignore DELETE
        response = client.delete("/room/r/2")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
