package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.RoomConnection
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Room connection table represents connection(s) between two Room entities.
 */
object RoomConnectionTable : IntIdTable("room_connections") {
    val fromRef = integer("fromRef").references(RoomTable.reference)
    val toRef = integer("toRef").references(RoomTable.reference)
}

class RoomConnectionDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomConnectionDAO>(RoomConnectionTable) {
        fun toRoomConnection(dao: RoomConnectionDAO) = RoomConnection(
            dao.fromRef,
            dao.toRef
        )
    }

    val fromRef by RoomConnectionTable.fromRef
    val toRef by RoomConnectionTable.toRef
}

class RoomConnectionRepo : RepoMultiEntryCore<RoomConnection> {
    override suspend fun allByRef(refId: Int): List<RoomConnection>? =
        RoomConnectionDAO
            .find{ (RoomConnectionTable.fromRef eq refId) }
            .map(RoomConnectionDAO::toRoomConnection)

    override suspend fun upsert(refId: Int, obj: RoomConnection): Boolean {
        TODO("Not yet implemented")
    }
}
