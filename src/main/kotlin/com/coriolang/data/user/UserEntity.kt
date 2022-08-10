package com.coriolang.data.user

import com.coriolang.data.todoitem.TodoItemEntity
import com.coriolang.data.todoitem.TodoItems
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<UserEntity>(Users)

    var username by Users.username
    var password by Users.password

    val todoItems by TodoItemEntity referrersOn TodoItems.user

    fun toSerializable() = User(
        username = username,
        password = password
    )
}