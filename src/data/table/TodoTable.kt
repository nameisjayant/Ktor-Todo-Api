package com.codingwithjks.data.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TodoTable: Table() {

    val id:Column<Int> = integer("id").autoIncrement()
    val userId:Column<Int> = integer("userId").references(UserTable.id)
    val todo:Column<String> = varchar("todo",512)
    val done:Column<Boolean> = bool("done")
}