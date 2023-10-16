package com.ne_rabotaem.features.login

import io.ktor.server.application.*
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.nio.ByteBuffer

@Serializable
data class LoginFormData(val login: String, val password: String)

fun Application.configureLoginRouting() {
    routing {
        get("/login") {
            call.respond(MustacheContent("login_sub.html", mapOf<String, String>()))
        }
        post("/login") {
            val loginController = LoginController(call)
            loginController.performLogin()
            val data = call.receive<LoginFormData>()
}
