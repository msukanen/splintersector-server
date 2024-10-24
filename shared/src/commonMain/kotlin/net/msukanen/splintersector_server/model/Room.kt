package net.msukanen.splintersector_server.model

import kotlinx.serialization.Serializable

/**
 * Room data lives here.
 */
@Serializable
class Room(val name: String, val refId: Int)
