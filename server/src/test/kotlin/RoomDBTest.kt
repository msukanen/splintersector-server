import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.db.srvonly.DATABASE_PASSWORD
import net.msukanen.splintersector_server.db.srvonly.DATABASE_URL
import net.msukanen.splintersector_server.db.srvonly.DATABASE_USER
import net.msukanen.splintersector_server.db.RoomConnectionRepo
import net.msukanen.splintersector_server.db.RoomRepo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RoomDBTest {
    private lateinit var db: Database

    @BeforeTest
    fun setupDatabase() {
        db = Database.connect(
            url = DATABASE_URL,
            user = DATABASE_USER,
            password = DATABASE_PASSWORD
        )
    }

    @Test
    fun `see if we get Room#2 data`() = runBlocking {
        val room = newSuspendedTransaction(db = db) {
            RoomRepo().byRef(2)
        }
        assertNotNull(room, "Room with refId 2 not found")
        Unit
    }

    @Test
    fun `see if we get Room#3 connections`() = runBlocking {
        val connections = newSuspendedTransaction(db = db) {
            RoomConnectionRepo().allByRef(3)
        }
        assertNotNull(connections)
        assertNotEquals(0, connections.size)
        connections.forEach {
            println(it)
        }
    }

    @Test
    fun `server should survive bad refIds`() = runBlocking {
        val room = newSuspendedTransaction(db = db) {
            RoomRepo().byRef(-100)
        }
        assertNull(room)
    }
}
