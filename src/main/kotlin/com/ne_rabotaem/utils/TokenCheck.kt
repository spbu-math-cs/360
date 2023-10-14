package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token
import io.ktor.server.application.*

object TokenCheck {
    fun isTokenValid(token: String): Boolean = Token.fetch(token) != null

    fun isTokenValid(call: ApplicationCall): Boolean {
        val token = call.request.headers["Bearer-Authorization"]

        if (!TokenCheck.isTokenValid(token.orEmpty())) {
            return false
        }

        return true
    }
}
