import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.db.RoomRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class StupidTest {
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
    fun seeIfWeGetData() = runBlocking {
        val room = newSuspendedTransaction(db = db) {
            RoomRepository().roomByRefId(2)
        }
        assertNotNull(room, "Room with refId 2 not found")
        Unit
    }
}
