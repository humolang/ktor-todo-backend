package com.coriolang

import com.coriolang.data.DatabaseSettings
import io.ktor.server.application.*
import com.coriolang.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

// generate keystore file using keytool
// write -ext SAN=ip:10.0.2.2 for successfully testing in android emulator
// keytool -keystore keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext SAN=ip:10.0.2.2

// export a certificate from a keystore
// keytool -export -alias sampleAlias -file extracas.crt -keystore keystore.jks

// use extracas.crt file in android network security config

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
