import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.db.RoomConnectionRepo
import net.msukanen.splintersector_server.db.RoomRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BasicDBConnectionTest {
    private lateinit var db: Database

    @BeforeTest
    fun setupDatabase() {
        db = Database.connect(
            url = "jdbc:mysql://localhost:3306/sss_test",
            user = "root",
            password = "pass1234"
        )
    }

    @Test
    fun `see if we get Room#2 data`() = runBlocking {
        val room = newSuspendedTransaction(db = db) {
            RoomRepository().byRef(2)
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
            RoomRepository().byRef(-100)
        }
        assertNull(room)
    }
}
