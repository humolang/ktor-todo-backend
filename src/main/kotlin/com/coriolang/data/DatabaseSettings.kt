package com.coriolang.data

import com.coriolang.data.tables.TodoItems
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSettings {

    fun init(
        url: String,
        driver: String,
        user: String,
        password: String
    ) {
        Database.connect(url, driver, user, password)

        transaction {
            SchemaUtils.create(TodoItems)
        }
    }
}