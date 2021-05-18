package com.codingwithjks.repository

import com.codingwithjks.data.dao.TodoDao
import com.codingwithjks.data.dao.UserDao
import com.codingwithjks.data.model.Todo
import com.codingwithjks.data.model.User
import com.codingwithjks.data.table.TodoTable
import com.codingwithjks.data.table.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepository : UserDao {

    override suspend fun createUser(email: String, name: String, password: String): User? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = UserTable.insert { user ->
                user[UserTable.email] = email
                user[UserTable.name] = name
                user[UserTable.password] = password
            }
        }
        return rowToResult(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int): User? =
        DatabaseFactory.dbQuery {
            UserTable.select { UserTable.id.eq(userId) }
                .map { rowToResult(it) }
                .singleOrNull()
        }


    override suspend fun findUserByEmail(email: String): User? =
        DatabaseFactory.dbQuery {
            UserTable.select { UserTable.email.eq(email) }
                .map { rowToResult(it) }
                .singleOrNull()
        }

    private fun rowToResult(row: ResultRow?): User? {
        if (row == null)
            return null
        return User(
            id = row[UserTable.id],
            email = row[UserTable.email],
            password = row[UserTable.password],
            name = row[UserTable.name]
        )
    }




}