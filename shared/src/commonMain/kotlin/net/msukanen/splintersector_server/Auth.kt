package net.msukanen.splintersector_server

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import net.msukanen.splintersector_server.model.AuthUser

@Serializable
data class AuthResponse(val token: String)

suspend fun authUser(user: AuthUser): AuthResponse = runBlocking {
    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    println("<AUTH> $user")
    val response = client.post("http://localhost:$SERVER_PORT/login") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
    println(response.body<String>())
    response.body<AuthResponse>()
}
