package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object TokenCheck {
    private fun isTokenValid(token: String): Boolean = Token.fetch(token) != null

    fun isTokenValid(call: ApplicationCall): Boolean {
        call.request.cookies.rawCookies.contains("token")
        if (!call.request.cookies.rawCookies.contains("token")) {
            return false
        }

        val token = call.request.cookies.rawCookies["token"]
        if (!TokenCheck.isTokenValid(token.orEmpty())) {
            return false
        }

        return true
    }
}
