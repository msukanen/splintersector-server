package net.msukanen.splintersector_server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 15551, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
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
