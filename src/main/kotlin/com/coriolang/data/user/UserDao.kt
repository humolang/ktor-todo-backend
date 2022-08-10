package com.coriolang.data.user

import org.jetbrains.exposed.sql.transactions.transaction

object UserDao {

    fun insert(username: String, password: String): User {
        val user = transaction {
            UserEntity.new {
                this.username = username
                this.password = password
            }
        }

        return user.toSerializable()
    }

    fun findByUsername(username: String): User? {
        val user = getEntityByUsername(username)
        return user?.toSerializable()
    }

    fun getEntityByUsername(username: String): UserEntity? {
        val user = transaction {
            UserEntity.all().find {
                it.username == username
            }
        }

        return user
    }

    fun hasUser(username: String) = findByUsername(username) != null
}