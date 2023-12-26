package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import io.ktor.server.application.*

object UserId {
    fun getId(token: String): Int? = Token.fetch(token)?.login?.let { User.getUserId(it) }

    fun getId(call: ApplicationCall): Int? = TokenCheck.getToken(call)
        ?.let { Token.fetch(it)?.login?.let { User.getUserId(it) } }
}