package com.ne_rabotaem.features.vote

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.grade.GradeDTO
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.demo.GradeReceiveRemote
import com.ne_rabotaem.utils.TokenCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class VoteController(val call: ApplicationCall) {
    suspend fun getPage() {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }
        call.respond(MustacheContent("voting.html", mapOf<String, String>()))
    }

    suspend fun getDemo(id: Int) {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        call.respond(Demo_grade.fetch(id).groupBy { it.teamId })
    }

    suspend fun getTeams(eventId: Int) {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        // var event = Event.fetch(eventId)
        // NOT WORKING! (fetch(1) returns null, but event with id == 1 is in table)

        if (Event.fetchAll().filter { it.eventId == eventId }.size == 0) {
            call.respond(HttpStatusCode.BadRequest, "No such event!")
            return;
        } // TODO: check, if current time not in [start, finish] (and date doesn't match), than send BadRequest

        call.respond(Team.fetchAll());
    }

    suspend fun vote() {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        val grade = call.receive<GradeReceiveRemote>()
        if (grade.comment.length > 500) {
            call.respond(HttpStatusCode.PayloadTooLarge, "Comment length must be less than 500 symbols!")
            return
        }
        if (grade.grade < 0 || grade.grade > 10) {
            call.respond(HttpStatusCode.PreconditionFailed, "Grade must be in range from 0 to 10!")
            return
        }

        val userId = User.getUserId(
            Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login
        )!!

        if (Demo_grade.fetch(grade.eventId, userId, grade.teamId) != null) {
            call.respond(HttpStatusCode.Conflict, "You have already rated!")
            return
        }

        Demo_grade.insert(
            GradeDTO(
                eventId = grade.eventId,
                personId = userId,
                teamId = grade.teamId,
                level = grade.level,
                grade = grade.grade,
                presentation = grade.presentation,
                additional = grade.additional,
                comment = grade.comment
            )
        )

        call.respond(HttpStatusCode.OK)
    }
}