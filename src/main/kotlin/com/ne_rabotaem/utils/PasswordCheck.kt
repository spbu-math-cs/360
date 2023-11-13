package com.ne_rabotaem.utils

import com.ne_rabotaem.database.user.User

object PasswordCheck {
    fun isPasswordValid(userId: Int, password: String): Boolean? {
        val userDTO = User.fetch(userId) ?: return null

        if (userDTO.password != password)
            return false

        return true
    }

    fun isPasswordValid(userLogin: String, password: String): Boolean? {
        val userDTO = User.fetch(userLogin) ?: return null

        if (userDTO.password != password)
            return false

        return true
    }
}