package net.msukanen.splintersector_server.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomConnection(val fromRef: Int, val toRef: Int)
