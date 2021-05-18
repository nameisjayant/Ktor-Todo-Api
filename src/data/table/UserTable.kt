package com.codingwithjks.data.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable : Table() {

    val id:Column<Int> = integer("id").autoIncrement()
    val email:Column<String> = varchar("email",128).uniqueIndex()
    val name:Column<String> = varchar("name",256)
    val password:Column<String> = varchar("password",64)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}