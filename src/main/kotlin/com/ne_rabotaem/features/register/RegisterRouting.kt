package com.ne_rabotaem.features.register

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {
    routing {
        get("/register") {
            RegisterController(call).getPage()
        }
        post("/register") {
            RegisterController(call).registerNewUser()
        }
    }
}
