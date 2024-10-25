package net.msukanen.splintersector_server.model

import kotlinx.serialization.Serializable

/**
 * Various user roles.
 */
enum class UserRole {
    /** Dungeon Master - Data Master - etc.*/
    DM,
    /** Bare-bones game player.*/
    Player
}

/**
 * User info container.
 */
@Serializable
data class User(
    /** User ID.*/
    val id: Int,
    /** Username.*/
    val name: String,
    /** User password.*/
    val pwd: String,
    /** User's role(s).*/
    val roles: List<UserRole>
)
