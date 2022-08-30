package com.coriolang

import com.coriolang.data.DatabaseSettings
import io.ktor.server.application.*
import com.coriolang.plugins.*
import io.ktor.network.tls.certificates.*
import java.io.File

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

// generate keystore file
//fun main(args: Array<String>) {
//    val keyStoreFile = File("build/keystore.jks")
//    val keystore = generateCertificate(
//        file = keyStoreFile,
//        keyAlias = "sampleAlias",
//        keyPassword = "foobar",
//        jksPassword = "foobar"
//    )
//}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val url = environment.config
        .property("ktor.database.url").getString()
    val driver = environment.config
        .property("ktor.database.driver").getString()
    val user = environment.config
        .property("ktor.database.user").getString()
    val password = environment.config
        .property("ktor.database.password").getString()

    DatabaseSettings.init(url, driver, user, password)

    configureSerialization()
    configureRouting()
    configureSecurity()
}
