package com.ne_rabotaem.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.mustache.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import java.io.File

fun Application.configureRouting() {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    routing {
        staticFiles("/", File("static"))

        get("/home") {
            call.respondText("Home")
        }
        get("/test") {
            call.respondText("Test passed!")
        }
    }
}
