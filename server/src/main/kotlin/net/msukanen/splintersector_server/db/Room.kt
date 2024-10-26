package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.Room
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

/** SQL ←→ Exposed.*/
object RoomTable : IntIdTable("rooms") {
    val name = varchar("name", 64)
    val reference = integer("refId")
}

/**
 * Room DAO for converting database stuff into concrete Room entities.
 */
class RoomDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomDAO>(RoomTable) {
        /** Create a [Room] from DAO data.*/
        fun toRoom(dao: RoomDAO) = Room(
            dao.name,
            dao.reference
        )
    }

    var name by RoomTable.name
    var reference by RoomTable.reference

    fun toRoom() = toRoom(this)
}

/**
 * Room repo accessor for manipulating database Room entries.
 */
class RoomRepo : RepoCore<Room> {
    /**
     * Find a [Room] by so called *reference ID*.
     *
     * @param[refId] Room #refID.
     */
    override suspend fun byRef(refId: Int): Room? = suspendTransaction {
        RoomDAO
            .find{ (RoomTable.reference eq refId) }
            .limit(1)
            .map(RoomDAO::toRoom)
            .firstOrNull()
    }

    /**
     * Update a [Room] in or insert into database.
     *
     * @param[refId] Room #refID
     * @param[obj] [Room]
     */
    override suspend fun upsert(refId: Int, obj: Room): Boolean {
        // 'UPDATE' if room refID exists...
        byRef(refId)?.let {
            RoomTable.update({ RoomTable.reference eq refId }) {
                it[name] = obj.name
            }
        } ?: {
        // ...and 'INSERT' if it doesn't.
            RoomTable.insert {
                it[name] = obj.name
                it[reference] = obj.reference
            }
        }
        return true
    }
}
