package com.ne_rabotaem.utils

import java.security.MessageDigest

object Hashing {
    @OptIn(ExperimentalStdlibApi::class)
    fun getHash(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest((data).toByteArray())

        return with(StringBuilder()) {
            digest.forEach {
                b -> append(b.toHexString())
            }
            toString().lowercase()
        }
    }
}