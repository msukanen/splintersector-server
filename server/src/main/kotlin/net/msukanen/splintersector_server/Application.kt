package net.msukanen.splintersector_server

import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.msukanen.splintersector_server.db.RoomRepo
import org.jetbrains.exposed.sql.Database
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.receive
import net.msukanen.splintersector_server.db.UserRepo
import net.msukanen.splintersector_server.model.AuthUser
import net.msukanen.splintersector_server.db.srvonly.DATABASE_PASSWORD
import net.msukanen.splintersector_server.db.srvonly.DATABASE_URL
import net.msukanen.splintersector_server.db.srvonly.DATABASE_USER

fun main() {
    embeddedServer(Netty, port = 15551, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val roomRepo = RoomRepo()
    val userRepo = UserRepo()
    configureDatabases()
    configureSerialization(roomRepo, userRepo)

    val serverInfo = """$SERVER_NAME @ $SERVER_HOST:$SERVER_PORT
                     |Running with ${getPlatform().name}
                     |  ↓
                     """.trimMargin()
    val usage = """Usage: /spnlsect/<path>?id=<value>
                |
                |Where:  <path>    - path or something...
                |        <id>      - some id along the <path>
                """.trimIndent()

    routing {
        get("/") {
            val response = """$serverInfo
                           |$usage   
                           """.trimMargin()
            call.respondText(response)
        }
        get("/splnsect") {
            val response = """$serverInfo
                           |$usage
                           """.trimMargin()
            call.respondText(response)
        }
    }
}

fun Application.configureDatabases() {
    Database.connect(
        url = DATABASE_URL,
        user = DATABASE_USER,
        password = DATABASE_PASSWORD
    )
}

fun Application.configureSerialization(
    roomRepo: RoomRepo,
    userRepo: UserRepo
) {
    install(ContentNegotiation) { json() }

    fun verifyTokenAndRole(token: String?, role: String): String? = try {
        "Yah"
    } catch (e: JWTVerificationException) {
        null
    }

    routing {
        route("/room") {
            // Room by reference ID.
            get("/r/{refId}") {
                call.parameters["refId"]?.toIntOrNull()
                    ?.let { roomRepo.byRef(it) }
                    ?.let { call.respond(it) }
                    ?: call.respond(HttpStatusCode.BadRequest)
            }

            post("/r/{refId}") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                verifyTokenAndRole(token, "DM")?.let {
                    // TODO: handle room update here.
                    call.respond(HttpStatusCode.OK)
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            post("/player/{playerId}/save") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                verifyTokenAndRole(token, "PLAYER")?.let {
                    // TODO: handle player save here.
                    call.respond(HttpStatusCode.OK)
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            post("/login") {
                val cred = call.receive<AuthUser>()
            }
        }
    }
}
