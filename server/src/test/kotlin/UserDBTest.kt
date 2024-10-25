import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.DATABASE_PASSWORD
import net.msukanen.splintersector_server.DATABASE_URL
import net.msukanen.splintersector_server.DATABASE_USER
import net.msukanen.splintersector_server.db.UserRepo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserDBTest {
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
    fun `see if we manage to fetch a User properly`() = runBlocking {
        val user = newSuspendedTransaction(db = db) {
            UserRepo().byName("Matti Meikalainen")
        }
        assertNotNull(user).also {
            assertEquals("Matti Meikalainen", user.name)
            assertEquals("pass1234", user.pwd)// this is set in scratches/users.sql (which isn't in repo).
            assertEquals("DM", user.roles[0].name)
        }
        Unit
    }
}
