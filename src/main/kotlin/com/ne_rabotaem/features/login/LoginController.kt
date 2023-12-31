package com.ne_rabotaem.features.login

import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.token.TokenDTO
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.Hashing
import com.ne_rabotaem.utils.PasswordCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

class LoginController(val call: ApplicationCall) {
    suspend fun getPage() {
        call.respond(MustacheContent("login.html", mapOf<String, String>()))
    }
    suspend fun performLogin() {
        val loginReceiveRemote = call.receive<LoginReceiveRemote>()

        val userDTO = User.fetch(loginReceiveRemote.login)
        if (userDTO == null) {
            call.respond(HttpStatusCode.Conflict, "User is not found!")
            return
        }

        println(userDTO.last_name)

        if (userDTO.password != Hashing.getHash(loginReceiveRemote.login + loginReceiveRemote.password)) {
            call.respond(HttpStatusCode.BadRequest, "Wrong password!")
            return
        }

        val token = UUID.randomUUID().toString()
        Token.insert(
            TokenDTO(
                login = loginReceiveRemote.login,
                token = token,
            ),
        )

        call.respond(LoginResponseRemote(token = token))
    }
}
