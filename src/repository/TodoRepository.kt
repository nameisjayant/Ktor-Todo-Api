package com.codingwithjks.repository

import com.codingwithjks.data.dao.TodoDao
import com.codingwithjks.data.model.Todo
import com.codingwithjks.data.table.TodoTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRepository : TodoDao {

    override suspend fun addTodo(userId: Int, todoData: String, done: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = TodoTable.insert { todo ->
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

    override suspend fun deleteAllTodoByUserId(userId: Int): Int =
        DatabaseFactory.dbQuery {
            TodoTable.deleteWhere { TodoTable.userId.eq(userId) }
        }

    override suspend fun deleteTodoById(id: Int): Int =
        DatabaseFactory.dbQuery {
            TodoTable.deleteWhere { TodoTable.id.eq(id) }
        }

    override suspend fun getTodoById(id: Int): Todo? =
        DatabaseFactory.dbQuery {
            TodoTable.select { TodoTable.id.eq(id) }
                .map {
                    rowToTodo(it)
                }.singleOrNull()
        }

    override suspend fun updateTodo(id: Int, todoData: String, done: Boolean): Int =
        DatabaseFactory.dbQuery {
            TodoTable.update({ TodoTable.id.eq(id) }) { todo ->
                todo[TodoTable.done] = done
                todo[TodoTable.todo] = todoData
            }
        }

    private fun rowToTodo(row: ResultRow?): Todo? {
        if (row == null)
            return null
        return Todo(
            id = row[TodoTable.id],
            userId = row[TodoTable.userId],
            todo = row[TodoTable.todo],
            done = row[TodoTable.done]
        )
    }
}