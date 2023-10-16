package com.ne_rabotaem.features.profile

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureProfileRouting() {
    routing {
        get("/profile") {
            val profileController = ProfileController(call)
            profileController.getProfile(call)
        }
    }
}