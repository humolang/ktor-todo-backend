package com.coriolang.data.todoitem

import com.coriolang.data.user.Users
import org.jetbrains.exposed.dao.id.UUIDTable

object TodoItems : UUIDTable() {

    val text = text("text")
    val importance = enumeration<Importance>("importance")
    val isCompleted = bool("is_completed")
    val creationDate = long("creation_date")
    val deadlineDate = long("deadline_date")
    val modificationDate = long("modification_date")

    val user = reference("user", Users)
}