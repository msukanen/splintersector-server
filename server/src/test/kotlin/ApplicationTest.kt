import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import net.msukanen.splintersector_server.model.Room
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `room can be found with refId`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/r/2")
        val results = response.body<Room>()

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
