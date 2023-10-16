package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.grade.GradeDTO
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoController(val call: ApplicationCall) {
    suspend fun getPage() {
        call.respond(MustacheContent("demo.html", mapOf<String, String>()))
    }

    suspend fun getDemos() {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        call.respond(Json.encodeToString(Event.fetchAll()))
    }

    suspend fun getDemo(id: Int) {
        if (!TokenCheck.isTokenValid(call))
            return

        call.respond(Demo_grade.fetch(id).groupBy { it.teamId })
    }

    suspend fun vote() {
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

        val userId = User.getUserId(
            Token.fetch(call.request.headers["Bearer-Authorization"]!!)!!.login
        )!!

        if (Demo_grade.fetch(grade.eventId, userId, grade.teamId) == null) {
            call.respond(HttpStatusCode.Conflict, "You have already rated!")
        }

        Demo_grade.insert(
            GradeDTO(
                eventId = grade.eventId,
                personId = userId,
                teamId = grade.teamId,
                grade = grade.grade,
                comment = grade.comment
            )
        )

        call.respond(HttpStatusCode.OK)
    }
}
