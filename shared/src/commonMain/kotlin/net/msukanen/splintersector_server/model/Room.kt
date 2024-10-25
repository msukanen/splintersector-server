package net.msukanen.splintersector_server.model

import kotlinx.serialization.Serializable

/**
 * Room data lives here.
 */
@Serializable
class Room(
    /**
     * Name of the room, preferably.
     *
     * This *should* be something that's meant for humans to read even
     * though there is no restrictions per se in place for the content.
     */
    var name: String,
    /**
     * Reference number.
     *
     * A unique value used for accessing the Room.
     */
    val reference: Int
)
