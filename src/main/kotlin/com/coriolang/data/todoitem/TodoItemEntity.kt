package com.coriolang.data.todoitem

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoItemEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TodoItemEntity>(TodoItems)

    var text by TodoItems.text
    var importance by TodoItems.importance
    var isCompleted by TodoItems.isCompleted
    var creationDate by TodoItems.creationDate
    var deadlineDate by TodoItems.deadlineDate
    var modificationDate by TodoItems.modificationDate

    fun toSerializable() = TodoItem(
        id = id.value,
        text = text,
        importance = importance,
        isCompleted = isCompleted,
        creationDate = creationDate,
        deadlineDate = deadlineDate,
        modificationDate = modificationDate
    )
}