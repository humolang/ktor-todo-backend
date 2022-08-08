package com.coriolang.data.todoitem

import org.jetbrains.exposed.sql.transactions.transaction

object TodoItemDao {

    fun getList(): List<TodoItem> = transaction {
        TodoItemEntity.all().map {
            it.toSerializable()
        }
    }

    fun updateList(todoItems: List<TodoItem>): List<TodoItem> {
        for (item in todoItems) {
            val serverItem = findById(item.id)

            if (serverItem == null) {
                insert(item)
                continue
            }

            if (serverItem.modificationDate < item.modificationDate) {
                update(item)
            }
        }

        return getList()
    }

    fun findById(id: Int): TodoItem? {
        val todoItem = transaction {
            TodoItemEntity.findById(id)
        }

        return todoItem?.toSerializable()
    }

    fun insert(todoItem: TodoItem): TodoItem {
        val item = transaction {
            TodoItemEntity.new {
                text = todoItem.text
                importance = todoItem.importance
                isCompleted = todoItem.isCompleted
                creationDate = todoItem.creationDate
                deadlineDate = todoItem.deadlineDate
                modificationDate = todoItem.modificationDate
            }
        }

        return item.toSerializable()
    }

    fun update(todoItem: TodoItem): TodoItem? {
        val item = transaction {
            val serverItem = TodoItemEntity.findById(todoItem.id)
                ?: return@transaction null

            serverItem.apply {
                text = todoItem.text
                importance = todoItem.importance
                isCompleted = todoItem.isCompleted
                creationDate = todoItem.creationDate
                deadlineDate = todoItem.deadlineDate
                modificationDate = todoItem.modificationDate
            }
        }

        return item?.toSerializable()
    }

    fun delete(id: Int): TodoItem? {
        return transaction {
            val serverItem = TodoItemEntity.findById(id)
                ?: return@transaction null

            val item = serverItem.toSerializable()
            serverItem.delete()

            item
        }
    }
}