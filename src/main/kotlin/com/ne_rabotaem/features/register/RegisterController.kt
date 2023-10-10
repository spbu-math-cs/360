package com.ne_rabotaem.features.register

import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.token.TokenDTO
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.database.user.UserDTO
import com.ne_rabotaem.database.user.rank
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RegisterController(val call: ApplicationCall) {
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()

        val userDTO = User.fetchUser(registerReceiveRemote.login)
        if (userDTO != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists!")
            return
        }

        val token = UUID.randomUUID().toString()

        User.insert(
            UserDTO(
                first_name = registerReceiveRemote.first_name,
                last_name = registerReceiveRemote.last_name,
                father_name = registerReceiveRemote.father_name,
                login = registerReceiveRemote.login,
                password = registerReceiveRemote.password,
                rank = rank.valueOf(registerReceiveRemote.rank)
            )
        )

        call.respond(HttpStatusCode.OK);
    }
}