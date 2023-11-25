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

        get("/demo/statistics/average") {
            DemoController(call).getTeamStatistics()
        }

        get("demo/statistics/comments") {
            DemoController(call).getComments()
        }

        get("demo/statistics/average/all") {
            DemoController(call).getAllTeamStatistics()
        }

        get("demo/statistics/comments/all") {
            DemoController(call).getAllComments()
        }
    }
}
