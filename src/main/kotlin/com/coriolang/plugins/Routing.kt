package com.coriolang.plugins

import com.coriolang.data.todoitem.TodoItem
import com.coriolang.data.todoitem.TodoItemDao
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    routing {
        route("/list") {
            // get list
            get {
                val list = TodoItemDao.getList()
                call.respond(list)
            }

            // update list
            patch {
                val list = try {
                    call.receive<List<TodoItem>>()
                } catch (e: ContentTransformationException) {
                    return@patch call.respondText(
                        "No content received",
                        status = HttpStatusCode.BadRequest
                    )
                }

                val updatedList = TodoItemDao.updateList(list)
                call.respond(updatedList)
            }

            // get element
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respondText(
                        "Missing id",
                        status = HttpStatusCode.BadRequest
                    )

                val item = TodoItemDao.findById(id)
                    ?: return@get call.respondText(
                        "Not found",
                        status = HttpStatusCode.NotFound
                    )

                call.respond(item)
            }

            // add element
            post {
                val item = try {
                    call.receive<TodoItem>()
                } catch (e: ContentTransformationException) {
                    return@post call.respondText(
                        "No content received",
                        status = HttpStatusCode.BadRequest
                    )
                }

                val insertedItem = TodoItemDao.insert(item)
                call.respond(insertedItem)
            }

            // change element
            put("/{id}") {
                call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respondText(
                        "Missing id",
                        status = HttpStatusCode.BadRequest
                    )

                val item = try {
                    call.receive<TodoItem>()
                } catch (e: ContentTransformationException) {
                    return@put call.respondText(
                        "No content received",
                        status = HttpStatusCode.BadRequest
                    )
                }

                val updatedItem = TodoItemDao.update(item)
                    ?: return@put call.respondText(
                        "Not found",
                        status = HttpStatusCode.NotFound
                    )

                call.respond(updatedItem)
            }

            // delete element
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respondText(
                        "Missing id",
                        status = HttpStatusCode.BadRequest
                    )

                val deletedItem = TodoItemDao.delete(id)
                    ?: return@delete call.respondText(
                        "Not found",
                        status = HttpStatusCode.NotFound
                    )

                call.respond(deletedItem)
            }
        }
    }
}
