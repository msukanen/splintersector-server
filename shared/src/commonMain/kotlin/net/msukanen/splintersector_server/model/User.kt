package net.msukanen.splintersector_server.model

import kotlinx.serialization.Serializable

enum class UserRole {
    DM,
    Player
}

/**
 * User info container.
 */
@Serializable
data class User(
    /**
     * User name.
     */
    val name: String,
    /**
     * User password.
     */
    val pwd: String,
    /**
     * User's role(s).
     */
    val roles: List<UserRole>
)
