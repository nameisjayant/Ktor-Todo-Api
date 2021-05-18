package com.codingwithjks.data.model

import io.ktor.auth.*

data class User(
    val id:Int,
    val name:String,
    val email:String,
    val password:String
) : Principal
