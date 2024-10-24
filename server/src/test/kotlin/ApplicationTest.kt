import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import net.msukanen.splintersector_server.configureDatabases
import net.msukanen.splintersector_server.configureSerialization
import net.msukanen.splintersector_server.db.RoomRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
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

        val response = client.get("/room/r/2")
        println("RESPONSE-BODY: ${response.body<String>()}")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
