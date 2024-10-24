package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.Room

interface RoomRepo {
    suspend fun roomByRefId(id: Int): Room?
    suspend fun roomByName(name: String): Room?
}

class RoomRepository : RoomRepo {
    override suspend fun roomByRefId(id: Int): Room? = suspendTransaction {
        RoomDAO.find {(RoomTable.refId eq id)}
            .limit(1)
            .map(::roomDaoToModel)
            .firstOrNull()
    }
    override suspend fun roomByName(name: String): Room? = suspendTransaction {
        RoomDAO.find {(RoomTable.name eq name)}
            .limit(1)
            .map(::roomDaoToModel)
            .firstOrNull()
    }
}
