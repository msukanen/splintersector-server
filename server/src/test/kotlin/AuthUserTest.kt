import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.AuthResponse
import net.msukanen.splintersector_server.authUser
import net.msukanen.splintersector_server.db.srvonly.DATABASE_PASSWORD
import net.msukanen.splintersector_server.db.srvonly.DATABASE_TEST_USER
import net.msukanen.splintersector_server.db.srvonly.DATABASE_TEST_PWD
import net.msukanen.splintersector_server.db.srvonly.DATABASE_URL
import net.msukanen.splintersector_server.db.srvonly.DATABASE_USER
import net.msukanen.splintersector_server.configureDatabases
import net.msukanen.splintersector_server.configureSerialization
import net.msukanen.splintersector_server.db.RoomRepo
import net.msukanen.splintersector_server.db.UserRepo
import net.msukanen.splintersector_server.model.AuthUser
import org.jetbrains.exposed.sql.Database
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthUserTest {
    private lateinit var db: Database

    @BeforeTest
    fun setup() {
        db = Database.connect(
            url = DATABASE_URL,
            user = DATABASE_USER,
            password = DATABASE_PASSWORD
        )
    }

    @Test
    fun `see that auth goes through`() = testApplication {
        application {
            configureDatabases()
            configureSerialization(RoomRepo(), UserRepo())
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user = AuthUser(DATABASE_TEST_USER, DATABASE_TEST_PWD)
        var response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        val loginResult = response.body<AuthResponse>()
        val token = loginResult.token
        response = client.get("/room/r/1") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `see that auth goes through with live server`() = runBlocking {
        val user = AuthUser(DATABASE_TEST_USER, DATABASE_TEST_PWD)
        val authResponse = authUser(user)
        println(authResponse.token)
    }
}
