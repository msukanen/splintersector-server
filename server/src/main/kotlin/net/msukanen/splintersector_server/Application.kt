package net.msukanen.splintersector_server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.msukanen.splintersector_server.db.RoomRepository
import org.jetbrains.exposed.sql.Database
import io.ktor.serialization.kotlinx.json.*
import net.msukanen.splintersector_server.model.Room

fun main() {
    embeddedServer(Netty, port = 15551, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val repository = RoomRepository()
    configureSerialization(repository)
    configureDatabases()

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
        url = "jdbc:mysql://localhost:3306/sss_test",
        user = "root",
        password = "pass1234"
    )
}

fun Application.configureSerialization(repository: RoomRepository) {
    install(ContentNegotiation) { json() }
    routing {
        route("/room") {
            // Room by reference ID.
            get("/r/{refId}") {
                call.parameters["refId"]?.toIntOrNull()
                    ?.let { repository.roomByRefId(it) }
                    ?.let { call.respond(it) }
                    ?: call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
