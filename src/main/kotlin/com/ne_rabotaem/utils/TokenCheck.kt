package com.ne_rabotaem.utils

import com.ne_rabotaem.database.token.Token

object TokenCheck {
    fun isTokenValid(token: String): Boolean = Token.fetch(token) != null
}