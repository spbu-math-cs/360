package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.team.Team
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDemoRouting() {
    routing {
        get("/demo") {
            DemoController(call).getPage()
        }
        get("/demo_list") {
            val demoController = DemoController(call)

            demoController.getDemos()
        }
    }
}
