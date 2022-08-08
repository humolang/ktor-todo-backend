package com.coriolang.data.todoitem

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(

    val id: Int = 0,
    val text: String = "",
    val importance: Importance = Importance.NORMAL,
    val isCompleted: Boolean = false,
    val creationDate: Long = 0L,
    val deadlineDate: Long = 0L,
    val modificationDate: Long = 0L
)