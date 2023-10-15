package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.team.Team
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDemoRouting() {
    routing {
        get("/demo") {
            val demoController = DemoController(call)

            if (call.request.queryParameters.contains("id")) {
                demoController.getDemo(call, Integer.parseInt(call.request.queryParameters["id"]!!))
                return@get
            }

            demoController.getDemos(call)
        }

        post("/demo/vote") {
            val demoController = DemoController(call)
            demoController.vote(call)
        }
    }
}
