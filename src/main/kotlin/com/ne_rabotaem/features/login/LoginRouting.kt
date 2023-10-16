package com.ne_rabotaem.features.login

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting() {
    routing {
        get("/login") {
            LoginController(call).getPage()
        }
        post("/login") {
            LoginController(call).performLogin()
        }
    }
}
