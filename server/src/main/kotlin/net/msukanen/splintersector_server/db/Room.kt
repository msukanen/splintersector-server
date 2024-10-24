package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.Room
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

/**
 * Room table.
 */
object RoomTable : IntIdTable("rooms") {
    val name = varchar("name", 64)
    val reference = integer("refId")
}

/**
 * Room DAO.
 */
class RoomDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomDAO>(RoomTable) {
        fun toRoom(dao: RoomDAO) = Room(
            dao.name,
            dao.reference
        )
    }

    var name by RoomTable.name
    var reference by RoomTable.reference
}

/**
 * Actual room repo accessor.
 */
class RoomRepository : RepoCore<Room> {
    override suspend fun byRef(id: Int): Room? = suspendTransaction {
        RoomDAO
            .find{ (RoomTable.reference eq id) }
            .limit(1)
            .map(RoomDAO::toRoom)
            .firstOrNull()
    }

    override suspend fun upsert(refId: Int, obj: Room): Boolean {
        if (byRef(refId) != null) {
            RoomTable.update({ RoomTable.reference eq refId }) {
                it[name] = obj.name
            }
        } else {
            RoomTable.insert {
                it[name] = obj.name
                it[reference] = obj.reference
            }
        }
        return true
    }
}
