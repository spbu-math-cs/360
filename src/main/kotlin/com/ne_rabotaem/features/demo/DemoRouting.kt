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

            if (call.request.queryParameters.contains("id")) {
                demoController.getDemo(Integer.parseInt(call.request.queryParameters["id"]!!))
                return@get
            }

            demoController.getDemos()
        }
    }
}
