package net.msukanen.splintersector_server

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String)
