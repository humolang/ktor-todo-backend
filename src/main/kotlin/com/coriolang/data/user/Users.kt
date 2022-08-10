package com.coriolang.data.user

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {

    val username = varchar("username", 32).uniqueIndex()
    val password = varchar("password", 255)
}