package com.ne_rabotaem.features.login

import io.ktor.server.application.*
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting() {
    routing {
        get("/login") {
            call.respond(MustacheContent("login.hbs", mapOf<String, String>()))
        }
        post("/login") {
            val loginController = LoginController(call)
            loginController.performLogin()
        }
    }
}
