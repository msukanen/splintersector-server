import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.DATABASE_PASSWORD
import net.msukanen.splintersector_server.DATABASE_URL
import net.msukanen.splintersector_server.DATABASE_USER
import net.msukanen.splintersector_server.db.UserRepo
import net.msukanen.splintersector_server.model.User
import net.msukanen.splintersector_server.model.UserRole
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserDBTest {
    private lateinit var db: Database
    private val repo by lazy { UserRepo() }
    private var user: User? = null

    @BeforeTest
    fun setupDatabase() {
        db = Database.connect(
            url = DATABASE_URL,
            user = DATABASE_USER,
            password = DATABASE_PASSWORD
        )
        user = runBlocking {
            newSuspendedTransaction(db = db) {
                repo.byName("Matti Meikalainen")
            }
        }
        assertNotNull(user)
    }

    @Test
    fun `see if we manage to fetch a User properly`() = runBlocking {
        user?.also {
            assertEquals("Matti Meikalainen", user!!.name)
            assertEquals("pass1234", user!!.pwd)// this is set in scratches/users.sql (which isn't in repo).
            assertEquals("DM", user!!.roles[0].name)
        }
        Unit
    }

    @Test
    fun `see if changing user role works`() = runBlocking {
        user?.also {
            val r = user!!.roles.toMutableList().apply { add(UserRole.Player) }
            user!!.roles = r
            newSuspendedTransaction {
                repo.upsert(user!!)
            }
        }
        val u = repo.byName("Matti Meikalainen")
        assertEquals("Player", u!!.roles[1].name)
    }
}
