package com.coriolang.data.entities

import com.coriolang.data.tables.TodoItems
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoItem(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TodoItem>(TodoItems)

    var text by TodoItems.text
    var importance by TodoItems.importance
    var isCompleted by TodoItems.isCompleted
    var creationDate by TodoItems.creationDate
    var deadlineDate by TodoItems.deadlineDate
    var modificationDate by TodoItems.modificationDate
}