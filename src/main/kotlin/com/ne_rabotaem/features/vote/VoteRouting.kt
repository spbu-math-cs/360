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

        // get("/demo/vote/grades") {
             // TODO: get current user grades
        // }

        // get("/demo/grades") {
        //     if (call.request.queryParameters.contains("id")) {
        //         VoteController(call).getDemo(Integer.parseInt(call.request.queryParameters["id"]!!))
        //     }
        //     else call.respond(HttpStatusCode.BadRequest, "Request must contain demo id!")
        // }

        get("/demo/vote/teams") {
            // Get a list of all the teams you can vote for
            if (call.request.queryParameters.contains("eventId")) {
                VoteController(call).getTeams(Integer.parseInt(call.request.queryParameters["eventId"]!!))
            }
            else call.respond(HttpStatusCode.BadRequest, "BadRequest: Request must contain eventId.")
        }

        post("/demo/vote") {
            VoteController(call).vote()
        }

        post("/demo/vote/inteam") {
            VoteController(call).inteamVote()
        }
    }
}