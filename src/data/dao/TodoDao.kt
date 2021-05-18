package com.codingwithjks.data.dao

import com.codingwithjks.data.model.Todo

interface TodoDao {

    suspend fun addTodo(
        userId:Int,
        todoData:String,
        done:Boolean
    ):Todo?

    suspend fun getAllTodo(
        userId: Int
    ):List<Todo>
}