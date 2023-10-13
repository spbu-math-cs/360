package com.ne_rabotaem.features.demo

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDemoRouting() {
    routing {
        post("/demo") {
            val demoController = DemoController(call)
            demoController.getDemos(call)
        }
    }
}