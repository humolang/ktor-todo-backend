package com.coriolang.plugins

import com.coriolang.data.todoitem.TodoItem
import com.coriolang.data.todoitem.TodoItemDao
import com.coriolang.data.user.PasswordUtils
import com.coriolang.data.user.User
import com.coriolang.data.user.UserDao
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import java.io.File
import java.util.UUID

fun Application.configureRouting() {
    routing {
        post("/registration") {
            val user = try {
                call.receive<User>()
            } catch (e: ContentTransformationException) {
                return@post call.respondText(
                    "No content received",
                    status = HttpStatusCode.BadRequest
                )
            }

            if (UserDao.findByUsername(user.username) != null) {
                return@post call.respondText(
                    "Username is already taken",
                    status = HttpStatusCode.Unauthorized
                )
            }

            UserDao.insert(
                user.username,
                PasswordUtils.hash(user.password)
            )

            call.respondText(
                "User created",
                status = HttpStatusCode.Created
            )
        }

        authenticate("auth-jwt") {
            route("/list") {
                // get list
                get {
                    val session = call.sessions.get<UserSession>()
                        ?: return@get call.respondText(
                            "Session doesn't exist or is expired",
                            status = HttpStatusCode.BadRequest
                        )

                    val user = UserDao.getEntityByUsername(session.username)
                        ?: return@get call.respondText(
                            "No such user",
                            status = HttpStatusCode.NotFound
                        )

                    val list = TodoItemDao.getList(user)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = list
                    )
                }

                // update list
                patch {
                    val session = call.sessions.get<UserSession>()
                        ?: return@patch call.respondText(
                            "Session doesn't exist or is expired",
                            status = HttpStatusCode.BadRequest
                        )

                    val user = UserDao.getEntityByUsername(session.username)
                        ?: return@patch call.respondText(
                            "No such user",
                            status = HttpStatusCode.NotFound
                        )

                    val list = try {
                        call.receive<List<TodoItem>>()
                    } catch (e: ContentTransformationException) {
                        return@patch call.respondText(
                            "No content received",
                            status = HttpStatusCode.BadRequest
                        )
                    }

                    val updatedList = TodoItemDao.updateList(list, user)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = updatedList
                    )
                }

                // get element
                get("/{id}") {
                    val id = call.parameters["id"]
                        ?: return@get call.respondText(
                            "Missing id",
                            status = HttpStatusCode.BadRequest
                        )

                    val uuid = UUID.fromString(id)
                    val item = TodoItemDao.findById(uuid)
                        ?: return@get call.respondText(
                            "Not found",
                            status = HttpStatusCode.NotFound
                        )

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = item
                    )
                }

                // add element
                post {
                    val session = call.sessions.get<UserSession>()
                        ?: return@post call.respondText(
                            "Session doesn't exist or is expired",
                            status = HttpStatusCode.BadRequest
                        )

                    val user = UserDao.getEntityByUsername(session.username)
                        ?: return@post call.respondText(
                            "No such user",
                            status = HttpStatusCode.NotFound
                        )

                    val item = try {
                        call.receive<TodoItem>()
                    } catch (e: ContentTransformationException) {
                        return@post call.respondText(
                            "No content received",
                            status = HttpStatusCode.BadRequest
                        )
                    }

                    val insertedItem = TodoItemDao.insert(item, user)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = insertedItem
                    )
                }

                // change element
                put("/{id}") {
                    val id = call.parameters["id"]
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

                    val uuid = UUID.fromString(id)
                    val updatedItem = TodoItemDao.update(uuid, item)
                        ?: return@put call.respondText(
                            "Not found",
                            status = HttpStatusCode.NotFound
                        )

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = updatedItem
                    )
                }

                // delete element
                delete("/{id}") {
                    val id = call.parameters["id"]
                        ?: return@delete call.respondText(
                            "Missing id",
                            status = HttpStatusCode.BadRequest
                        )

                    val uuid = UUID.fromString(id)
                    val deletedItem = TodoItemDao.delete(uuid)
                        ?: return@delete call.respondText(
                            "Not found",
                            status = HttpStatusCode.NotFound
                        )

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = deletedItem
                    )
                }
            }
        }

        static(".well-known") {
            staticRootFolder = File("certs")
            file("jwks.json")
        }
    }
}
