package com.ne_rabotaem.features.home

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureHomeRouting() {
    routing {
        get("/") {
            call.respond(MustacheContent("home.html", mapOf<String, String>()))
        }
    }
}