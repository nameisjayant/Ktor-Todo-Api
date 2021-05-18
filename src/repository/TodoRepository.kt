package com.codingwithjks.repository

import com.codingwithjks.data.dao.TodoDao
import com.codingwithjks.data.model.Todo
import com.codingwithjks.data.table.TodoTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRepository : TodoDao {

    override suspend fun addTodo(userId: Int, todoData: String, done: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = TodoTable.insert { todo->
                todo[TodoTable.userId] = userId
                todo[TodoTable.todo] = todoData
                todo[TodoTable.done] = done
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    override suspend fun getAllTodo(userId: Int): List<Todo> =
        DatabaseFactory.dbQuery {
            TodoTable.select { TodoTable.userId.eq(userId) }
                .mapNotNull { rowToTodo(it) }
        }


    private fun rowToTodo(row: ResultRow?): Todo?{
        if(row == null)
            return null
        return Todo(
            id = row[TodoTable.id],
            userId = row[TodoTable.userId],
            todo = row[TodoTable.todo],
            done = row[TodoTable.done]
        )
    }
}