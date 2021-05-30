package com.codingwithjks.data.dao

import com.codingwithjks.data.model.Todo
import com.codingwithjks.data.model.User

interface TodoDao {

    suspend fun addTodo(
        userId:Int,
        todoData:String,
        done:Boolean
    ):Todo?

    suspend fun getAllTodo(
        userId: Int
    ):List<Todo>

    suspend fun deleteAllTodoByUserId(userId: Int):Int

    suspend fun deleteTodoById(id:Int):Int

    suspend fun getTodoById(
        id:Int
    ):Todo?

    suspend fun updateTodo(id:Int,todo:String,done: Boolean):Int?

}