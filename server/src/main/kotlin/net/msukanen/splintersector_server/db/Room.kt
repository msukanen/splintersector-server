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
 *
 * Mirrors, more or less, the 'rooms' table in the underlying database.
 */
object RoomTable : IntIdTable("rooms") {
    val name = varchar("name", 64)
    val reference = integer("refId")
}

/**
 * Room DAO for converting database stuff into concrete Room entities.
 */
class RoomDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomDAO>(RoomTable) {
        /**
         * Create a [Room] from DAO data.
         *
         * NOTE: Client will *never* know about DAO internals - therefore the decoupling.
         */
        fun toRoom(dao: RoomDAO) = Room(
            dao.name,
            dao.reference
        )
    }

    var name by RoomTable.name
    var reference by RoomTable.reference
}

/**
 * Room repo accessor for manipulating database Room entries.
 */
class RoomRepository : RepoCore<Room> {
    /**
     * Find a [Room] by so called *reference ID*.
     */
    override suspend fun byRef(id: Int): Room? = suspendTransaction {
        RoomDAO
            .find{ (RoomTable.reference eq id) }
            .limit(1)
            .map(RoomDAO::toRoom)
            .firstOrNull()
    }

    /**
     * Update/insert a [Room] in/into database.
     */
    override suspend fun upsert(refId: Int, obj: Room): Boolean {
        // 'UPDATE' if room refID exists...
        if (byRef(refId) != null) {
            RoomTable.update({ RoomTable.reference eq refId }) {
                it[name] = obj.name
            }
        }
        // ...and 'INSERT' if it doesn't.
        else {
            RoomTable.insert {
                it[name] = obj.name
                it[reference] = obj.reference
            }
        }
        return true
    }
}
