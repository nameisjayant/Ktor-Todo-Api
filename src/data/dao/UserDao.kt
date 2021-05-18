package com.codingwithjks.data.dao

import com.codingwithjks.data.model.User

interface UserDao {

    suspend fun createUser(
        email:String,
        name:String,
        password:String
    ):User?

   suspend fun findUser(
        userId:Int
    ):User?

   suspend fun findUserByEmail(
        email:String
    ):User?
}