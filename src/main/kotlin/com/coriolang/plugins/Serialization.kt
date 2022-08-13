package com.coriolang.plugins

import io.ktor.serialization.kotlinx.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
