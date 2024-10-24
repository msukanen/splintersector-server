package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.Room
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Room table.
 */
object RoomTable : IntIdTable("rooms") {
    val name = varchar("name", 64)
    val refId = integer("refId")
}

/**
 * Room DAO.
 */
class RoomDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomDAO>(RoomTable)

    var name by RoomTable.name
    var refId by RoomTable.refId
}

/**
 * RoomDAO → Room conversion.
 */
fun roomDaoToModel(dao: RoomDAO) = Room(
    dao.name,
    dao.refId
)
