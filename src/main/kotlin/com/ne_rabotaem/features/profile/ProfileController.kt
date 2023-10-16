package com.ne_rabotaem.features.profile

import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*

class ProfileController(val call: ApplicationCall) {
    suspend fun getProfile(call: ApplicationCall) {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        val userDTO = User.fetch(Token.fetch(call.request.headers["Bearer-Authorization"]!!)!!.login)
        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        }

        call.respond(MustacheContent("profile.hbs", mapOf<String, String>(
            "first_name" to userDTO!!.first_name,
            "last_name" to userDTO!!.last_name,
            "father_name" to userDTO!!.father_name
        )))
    }
}
