package com.coriolang.data.todoitem

import com.coriolang.data.user.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object TodoItemDao {

    fun getList(user: UserEntity): List<TodoItem> = transaction {
        user.todoItems.map {
            it.toSerializable()
        }
    }

    fun updateList(todoItems: List<TodoItem>, user: UserEntity): List<TodoItem> {
        for (item in todoItems) {
            val id = UUID.fromString(item.id)
            val serverItem = findById(id)

            if (serverItem == null) {
                insert(item, user)
                continue
            }

            if (serverItem.modificationDate < item.modificationDate) {
                update(id, item)
            }
        }

        return getList(user)
    }

    fun findById(id: UUID): TodoItem? {
        val todoItem = transaction {
            TodoItemEntity.findById(id)
        }

        return todoItem?.toSerializable()
    }

    fun insert(todoItem: TodoItem, user: UserEntity): TodoItem {
        val id = UUID.fromString(todoItem.id)

        val item = transaction {
            TodoItemEntity.new(id) {
                text = todoItem.text
                importance = todoItem.importance
                isCompleted = todoItem.isCompleted
                creationDate = todoItem.creationDate
                deadlineDate = todoItem.deadlineDate
                modificationDate = todoItem.modificationDate
                this.user = user
            }
        }

        return item.toSerializable()
    }

    fun update(id: UUID, todoItem: TodoItem): TodoItem? {
        val item = transaction {
            val serverItem = TodoItemEntity.findById(id)
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

    fun delete(id: UUID): TodoItem? {
        return transaction {
            val serverItem = TodoItemEntity.findById(id)
                ?: return@transaction null

            val item = serverItem.toSerializable()
            serverItem.delete()

            item
        }
    }
}