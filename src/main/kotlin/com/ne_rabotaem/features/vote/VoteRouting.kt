package com.ne_rabotaem.features.vote

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureVoteRouting() {
    routing {
        get("/demo/vote") {
            VoteController(call).getPage()
        }
        post("/demo/vote") {
            VoteController(call).vote()
        }
    }
}