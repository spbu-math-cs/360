package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.grade.GradeDTO
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoController(val call: ApplicationCall) {
    suspend fun getDemos(call: ApplicationCall) {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        call.respond(Json.encodeToString(Event.fetchAll()))
    }

    suspend fun getDemo(call: ApplicationCall, id: Int) {
        if (!TokenCheck.isTokenValid(call))
            return

        call.respond(Demo_grade.fetch(id).groupBy { it.teamId })
    }

    suspend fun vote(call: ApplicationCall) {
        if (!TokenCheck.isTokenValid(call))
            return

        val grade = call.receive<GradeReceiveRemote>()
        if (grade.comment.length > 500) {
            call.respond(HttpStatusCode.PayloadTooLarge, "Comment length must be less than 500 symbols!")
            return
        }
        if (grade.grade < 0 || grade.grade > 10) {
            call.respond(HttpStatusCode.PreconditionFailed, "Grade must be in range from 0 to 10!")
        }

        Demo_grade.insert(
            GradeDTO(
                eventId = grade.eventId,
                personId = User.getUserId(
                    Token.fetch(call.request.headers["Bearer-Authorization"]!!)!!.login
                )!!,
                teamId = grade.teamId,
                grade = grade.grade,
                comment = grade.comment
            )
        )

        call.respond(HttpStatusCode.OK)
    }
}
