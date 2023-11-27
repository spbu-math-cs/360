package com.ne_rabotaem.features.vote

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureVoteRouting() {
    routing {
        get("/demo/vote") {
            if (call.request.queryParameters.contains("eventId")) {
                VoteController(call).getPage(Integer.parseInt(call.request.queryParameters["eventId"]!!))
            }
            else call.respond(HttpStatusCode.BadRequest, "BadRequest: Request must contain eventId.")
        }

        get("/demo/vote/teams") {
            // Get a list of all the teams you can vote for

            if (call.request.queryParameters.contains("eventId")) {
                VoteController(call).getTeams()
            }
            else call.respond(HttpStatusCode.BadRequest, "BadRequest: Request must contain eventId.")
        }

        post("/demo/vote") {
            VoteController(call).vote()
        }

        post("/demo/vote/inteam") {
            VoteController(call).inteamVote()
        }

        get("/demo/vote/grades") {
            VoteController(call).getDemoGrades()
        }

        get("/demo/vote/inteam/grades") {
            VoteController(call).getInTeamGrades()
        }
    }
}