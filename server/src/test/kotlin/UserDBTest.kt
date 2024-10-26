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
import kotlin.test.assertTrue

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
        // Add "Player" role.
        user?.also {
            val r = user!!.roles.toMutableList().apply { add(UserRole.Player) }
            user!!.roles = r
            newSuspendedTransaction {
                repo.upsert(user!!)
            }
        }

        // re-retrieve the User, just in case of i.e. caching interfering otherwise.
        var usr = repo.byName("Matti Meikalainen")
        assertNotNull(usr).also {
            assertTrue(usr.has(UserRole.Player))
        }
        // Drop back to "DM"-only as per defined in scratches/users.sql
        user!!.roles = listOf(UserRole.DM)
        newSuspendedTransaction {
            repo.upsert(user!!)
        }
        // re-retrieve the User; see that they have *only* "DM" set.
        usr = repo.byName("Matti Meikalainen")
        assertNotNull(usr).also {
            assertEquals(1, usr.roles.size)
            assertTrue(usr.has(UserRole.DM))
        }
        Unit//due assertNotNull()
    }
}
