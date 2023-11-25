package com.ne_rabotaem.features.demo

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDemoRouting() {
    routing {
        get("/demo") {
            DemoController(call).getPage()
        }

        get("/demo_list") {
            DemoController(call).getDemos()
        }
    }
}
