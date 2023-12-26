package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token
import io.ktor.server.application.*

object TokenCheck {
    private fun isTokenValid(token: String): Boolean = Token.fetch(token) != null

    fun getToken(call: ApplicationCall): String? {
        call.request.cookies.rawCookies.contains("token")
        if (!call.request.cookies.rawCookies.contains("token")) {
            return null
        }

        return call.request.cookies.rawCookies["token"]
    }
    fun isTokenValid(call: ApplicationCall): Boolean {
        val token = getToken(call)

        if (!isTokenValid(token.orEmpty())) {
            return false
        }

        return true
    }
}

