package com.ne_rabotaem.features.profile

import com.ne_rabotaem.features.login.LoginController
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProfileRouting() {
    routing {
        get("/profile") {
            val profileController = ProfileController(call)
            profileController.getProfile(call)
        }
    }
}