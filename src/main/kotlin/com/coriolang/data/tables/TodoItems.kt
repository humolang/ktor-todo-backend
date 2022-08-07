package com.coriolang.data.tables

import com.coriolang.data.Importance
import org.jetbrains.exposed.dao.id.IntIdTable

object TodoItems : IntIdTable() {

    val text = text("text")
    val importance = enumeration<Importance>("importance")
    val isCompleted = bool("is_completed")
    val creationDate = long("creation_date")
    val deadlineDate = long("deadline_date")
    val modificationDate = long("modification_date")
}