package com.ne_rabotaem.features.register

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {
    routing {
        get("/register") {
            call.respond(MustacheContent("register_sub.html", mapOf<String, String>()))
        }
        post("/register") {
            val registerController = RegisterController(call)
            registerController.registerNewUser()
        }
    }
}
