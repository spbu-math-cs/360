package com.ne_rabotaem.features.demo.statistics

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureStatisticsRouting() {
    routing {
        get("/demo/statistics") {
            StatisticsController(call).getPage()
        }

        get("/demo/statistics/average") {
            StatisticsController(call).getTeamStatistics()
        }

        get("demo/statistics/comments") {
            StatisticsController(call).getComments()
        }

        get("demo/statistics/average/all") {
            StatisticsController(call).getAllTeamStatistics()
        }

        get("demo/statistics/comments/all") {
            StatisticsController(call).getAllComments()
        }
    }
}