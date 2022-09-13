package com.coriolang.plugins

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.coriolang.data.user.PasswordUtils
import com.coriolang.data.user.User
import com.coriolang.data.user.UserDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val privateKeyString = environment.config.property("ktor.jwt.privateKey").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()
    val myRealm = environment.config.property("ktor.jwt.realm").getString()

    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    val createToken = { username: String ->
        val publicKey = jwkProvider.get("858fc477-8be9-ed64-038d-07cc2db6c6c0").publicKey
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString))
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpecPKCS8)

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 15778800000)) // 8 hours = 28800000 ms; 6 months = 15778800000 ms
            .sign(Algorithm.RSA256(publicKey as RSAPublicKey, privateKey as RSAPrivateKey))

        token
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm

            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { defaultScheme, realm ->
                call.respondText(
                    "Token is not valid or has expired",
                    status = HttpStatusCode.Unauthorized
                )
            }
        }
    }

    install(Sessions) {
        cookie<UserSession>("user_session")
    }

    routing {
        post("/login") {
            val user = try {
                call.receive<User>()
            } catch (e: ContentTransformationException) {
                return@post call.respondText(
                    "No content received",
                    status = HttpStatusCode.BadRequest
                )
            }

            val existingUser = UserDao.findByUsername(user.username)
                ?: return@post call.respondText(
                    "No such user",
                    status = HttpStatusCode.Unauthorized
                )

            if (existingUser.password != PasswordUtils.hash(user.password)) {
                return@post call.respondText(
                    "Not correct password",
                    status = HttpStatusCode.Unauthorized
                )
            }

            call.sessions.set(
                UserSession(user.username)
            )

            val token = createToken(user.username)

            call.respond(
                status = HttpStatusCode.OK,
                message = hashMapOf("token" to token)
            )
        }

        post("/authorize") {
            val username = try {
                call.receiveText()
            } catch (e: ContentTransformationException) {
                return@post call.respondText(
                    "No content received",
                    status = HttpStatusCode.BadRequest
                )
            }

            call.sessions.set(
                UserSession(username)
            )

            val token = createToken(username)

            call.respond(
                status = HttpStatusCode.OK,
                message = hashMapOf("token" to token)
            )
        }
    }
}