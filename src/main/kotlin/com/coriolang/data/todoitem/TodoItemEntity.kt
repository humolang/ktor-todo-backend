package com.coriolang.data.todoitem

import com.coriolang.data.user.UserEntity
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class TodoItemEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {

    companion object : UUIDEntityClass<TodoItemEntity>(TodoItems)

    var text by TodoItems.text
    var importance by TodoItems.importance
    var isCompleted by TodoItems.isCompleted
    var creationDate by TodoItems.creationDate
    var deadlineDate by TodoItems.deadlineDate
    var modificationDate by TodoItems.modificationDate

    var user by UserEntity referencedOn TodoItems.user

    fun toSerializable() = TodoItem(
        id = id.value.toString(),
        text = text,
        importance = importance,
        isCompleted = isCompleted,
        creationDate = creationDate,
        deadlineDate = deadlineDate,
        modificationDate = modificationDate
    )
}