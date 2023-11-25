package com.ne_rabotaem.features.demo.statistics

import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import com.ne_rabotaem.utils.UserCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StatisticsController(val call: ApplicationCall) {
    private val isUserValid get() = UserCheck.isUserExists(call)
    private val userId: Int

    init {
        runBlocking {
            if (!isUserValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }

        userId = UserId.getId(call)!!
    }

    suspend fun getPage() {
        call.respond(MustacheContent("statistics.html", mapOf<String, String>()))
    }

    private fun getTeamAndEvent(): StatisticsParameters? {
        val teamId = PersonTeam.getTeam(userId) ?: return null
        val eventId = call.parameters["eventId"]?.toInt() ?: return null

        return StatisticsParameters(eventId, teamId)
    }

    suspend fun getTeamStatistics() {
        val parameters = getTeamAndEvent()

        if (parameters == null){
            call.respond(HttpStatusCode.BadRequest, "Invalid team or event id!")
            return
        }

        call.respond(
            Json.encodeToString(
                Demo_grade.getAverage(
                    parameters.eventId,
                    parameters.teamId
                )
            )
        )
    }

    suspend fun getComments() {
        val parameters = getTeamAndEvent()

        if (parameters == null){
            call.respond(HttpStatusCode.BadRequest, "Invalid team or event id!")
            return
        }

        call.respond(
            Json.encodeToString(
                Demo_grade.getComments(
                    parameters.eventId,
                    parameters.teamId
                )
            )
        )
    }

    suspend fun getAllTeamStatistics() {
        if (!User.checkSuperUser(userId)){
            call.respond(HttpStatusCode.Forbidden, "You must have superuser rights!")
            return
        }

        val eventId = call.parameters["eventId"]?.toInt()
        if (eventId == null) {
            call.respond("Query parameters must contain event id!")
        }

        call.respond(Json.encodeToString(Demo_grade.getAverage(eventId!!)))
    }

    suspend fun getAllComments() {
        if (!User.checkSuperUser(userId)){
            call.respond(HttpStatusCode.Forbidden, "You must have superuser rights!")
            return
        }

        val eventId = call.parameters["eventId"]?.toInt()
        if (eventId == null) {
            call.respond("Query parameters must contain event id!")
        }

        call.respond(Json.encodeToString(Demo_grade.getComments(eventId!!)))
    }
}
