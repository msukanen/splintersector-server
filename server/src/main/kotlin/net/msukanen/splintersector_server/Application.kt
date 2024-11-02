package net.msukanen.splintersector_server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
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
import net.msukanen.splintersector_server.db.srvonly.JWT_SECRET
import net.msukanen.splintersector_server.model.UserRole

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, module = Application::module)
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
    suspend fun usage(call: RoutingCall) {
        val use = """Usage: /spnlsect/<path>?id=<value>
                |
                |Where:  <path>    - path or something...
                |        <id>      - some id along the <path>
                """.trimIndent()
        call.respondText("""$serverInfo
                           |$use   
                           """.trimMargin())
    }

    routing {
        get("/") { usage(call)}
        get("/splnsect") { usage(call)}
        get("/help") { usage(call)}
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

    fun verifyTokenAndRole(token: String?, role: UserRole): String? = try {
        with(JWT
            .require(Algorithm.HMAC512(JWT_SECRET))
            .build()
            .verify(token)) {
                getClaim("roles")
                    .asList(String::class.java)
                    .takeIf { it.contains(role.name) }
                    ?.let { this.subject }
        }
    } catch (e: JWTVerificationException) {
        null
    }

    routing {
        route("/room") {
            // Room by reference ID.
            get("/r/{refId}") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                verifyTokenAndRole(token, UserRole.DM)?.let {
                    call.parameters["refId"]?.toIntOrNull()
                        ?.let { roomRepo.byRef(it) }
                        ?.let { call.respond(it) }
                        ?: call.respond(HttpStatusCode.BadRequest)
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            post("/r/{refId}") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                verifyTokenAndRole(token, UserRole.DM)?.let {
                    // TODO: handle room update here.
                    call.respond(HttpStatusCode.OK)
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            post("/player/{playerId}/save") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                verifyTokenAndRole(token, UserRole.Player)?.let {
                    // TODO: handle player save here.
                    call.respond(HttpStatusCode.OK)
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }
        }

        route("/") {
            post("/login") {
                val cred = call.receive<AuthUser>()
                userRepo.authenticate(cred)?.let {
                    val token = {
                        val algo = Algorithm.HMAC512(JWT_SECRET)
                        JWT.create()
                            .withSubject(it.id.toString())
                            .withClaim("roles", it.roles.map { it.name })
                            .sign(algo)
                    }
                    println("<DEBUG> tok: $token")
                    call.respond(AuthResponse(token()))
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
