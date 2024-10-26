package net.msukanen.splintersector_server.db

import net.msukanen.splintersector_server.model.User
import net.msukanen.splintersector_server.model.UserRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert

object UserTable : IntIdTable("users") {
    val name = varchar("name", 64)
    val pwd = varchar("pwd", 128)
}

object UserRoleTable : IntIdTable("user_roles") {
    val userId = reference("userId", id)
    var role = varchar("role", 16)
}

class UserRoleDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserRoleDAO>(UserRoleTable) {
        fun toUserRole(dao: UserRoleDAO): UserRole = UserRole.valueOf(dao.role)
    }

    var role by UserRoleTable.role
}

class UserDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable) {
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
}

class UserRepo {
    suspend fun byName(name: String): User? = suspendTransaction {
        UserDAO
            .find { (UserTable.name eq name) }
            .limit(1)
            .map(UserDAO::toUser)
            .firstOrNull()
    }

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
}
