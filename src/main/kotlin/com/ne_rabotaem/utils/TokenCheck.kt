package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object TokenCheck {
    fun isTokenValid(token: String): Boolean = Token.fetch(token) != null

    suspend fun isTokenValid(call: ApplicationCall): Boolean {
        if (!call.request.headers.contains("Bearer-Authorization")) {
            call.respond(HttpStatusCode.NoContent)
            return false
        }

        val token = call.request.headers["Bearer-Authorization"]
        if (!TokenCheck.isTokenValid(token.orEmpty())) {
            call.respond(HttpStatusCode.Unauthorized)
            return false
        }

        return true
    }
}
