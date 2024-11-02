package net.msukanen.splintersector_server.db

import kotlinx.coroutines.runBlocking
import net.msukanen.splintersector_server.model.AuthUser
import net.msukanen.splintersector_server.model.User
import net.msukanen.splintersector_server.model.UserRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/** SQL ←→ Exposed */
object UserTable : IntIdTable("users") {
    val name = varchar("name", 64)
    val pwd = varchar("pwd", 128)
}

/** SQL ←→ Exposed */
object UserRoleTable : IntIdTable("user_roles") {
    val userId = reference("userId", id)
    var role = varchar("role", 16)
}

/**
 * DAO for [UserRole] related activity.
 */
class UserRoleDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserRoleDAO>(UserRoleTable) {
        fun toUserRole(dao: UserRoleDAO): UserRole = UserRole.valueOf(dao.role)
    }

    var role by UserRoleTable.role
}

/**
 * DAO for [User] related activity.
 */
class UserDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable) {
        /** Convert DAO into concrete User entity.*/
        fun toUser(dao: UserDAO): User {
            val roles = UserRoleDAO
                .find { (UserRoleTable.userId eq dao.id )}
                .map(UserRoleDAO::toUserRole)
            return User(
                dao.id.value,
                dao.name,
                dao.pwd,
                roles
            )
        }
    }
    val name by UserTable.name
    val pwd by UserTable.pwd

    fun toUser() = toUser(this)
}

/**
 * User repository.
 */
class UserRepo {
    /**
     * Get a [User] by name.
     *
     * @param[name] name of the user to look for.
     * @return [User] or `null`.
     */
    suspend fun byName(name: String): User? = suspendTransaction {
        println("<BYNAME> $name")
        UserDAO
            .find { (UserTable.name eq name) }
            .limit(1)
            .map(UserDAO::toUser)
            .firstOrNull()
    }

    /**
     * Update an existing [User] in (or insert a new one into) database.
     *
     * @param[user] [User] to update/insert.
     */
    suspend fun upsert(user: User) {
        byName(user.name)?.let {
            UserRoleTable.deleteWhere { userId eq user.id }
            user.roles.forEach { r ->
                UserRoleTable.insert {
                    it[userId] = user.id
                    it[role] = r.toString()
                }
            }
        }
    }

    /**
     * Attempt to authenticate the given [AuthUser], giving us [User] to work with.
     * If authentication fails we return `null`.
     * @TODO better/different failure case behavior.
     *
     * @param[authUser] some [AuthUser] data.
     * @return [User] or `null`.
     */
    fun authenticate(authUser: AuthUser): User? = runBlocking {
        newSuspendedTransaction {
            byName(authUser.name)?.takeIf { it.pwd == authUser.pwd }
        }
    }
}
