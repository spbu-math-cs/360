package com.ne_rabotaem.utils

import io.ktor.server.application.*

object UserCheck {
    fun isUserExists(call: ApplicationCall): Boolean {
        return TokenCheck.isTokenValid(call) && UserId.getId(call) != null
    }
}