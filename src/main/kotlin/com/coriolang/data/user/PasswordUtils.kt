package com.coriolang.data.user

import java.math.BigInteger
import java.security.MessageDigest

object PasswordUtils {

    fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")

        return BigInteger(1, messageDigest.digest(input.toByteArray()))
            .toString(16)
            .padStart(32, '0')
    }
}