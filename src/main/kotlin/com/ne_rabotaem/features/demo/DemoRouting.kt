package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.team.Team
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

        get("/demo/statistics") {
            DemoController(call).getStatisticsPage()
        }

        post("/demo/statistics/average") {
            DemoController(call).getTeamStatistics()
        }

        post("demo/statistics/comments") {
            DemoController(call).getComments()
        }

        post("demo/statistics/average/all") {
            DemoController(call).getAllTeamStatistics()
        }

        post("demo/statistics/comments/all") {
            DemoController(call).getAllComments()
        }
    }
}
