package com.ne_rabotaem.features.profile

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureProfileRouting() {
    routing {
        get("/profile") {
            ProfileController(call).getProfile()
        }

        get("/profile/team/info") {
            ProfileController(call).getTeam()
        }

        post("profile/team/leave") {
            ProfileController(call).leave()
        }

        get("profile/team/invite") {
            ProfileController(call).getInvites()
        }

        post("profile/team/invite/send") {
            ProfileController(call).invite()
        }

        post("profile/team/invite/answer") {
            ProfileController(call).answer()
        }

        post("profile/change/password") {
            ProfileController(call).changePassword()
        }

        get("profile/id") {
             ProfileController(call).getId()
        }
    }
}