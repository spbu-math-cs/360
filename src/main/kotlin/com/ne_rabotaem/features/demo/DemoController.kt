package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoController(val call: ApplicationCall) {
    private val isTokenValid get() = TokenCheck.isTokenValid(call)
    private val userId: Int

    init {
        runBlocking {
            if (!isTokenValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }

        userId = UserId.getId(call)!!
    }

    suspend fun getPage() {
        call.respond(MustacheContent("demo.html", mapOf<String, String>()))
    }

    suspend fun getStatisticsPage() {
        call.respond(MustacheContent("statistics.html", mapOf<String, String>()))
    }

    suspend fun getDemos() {
        call.respond(Json.encodeToString(Event.fetchAll()))
    }

    suspend fun getTeamStatistics() {
        val teamId = PersonTeam.getTeam(userId)
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "You must be on a team!")
            return
        }

        val statisticsReceiveRemote = call.receive<StatisticsReceiveRemote>()
        call.respond(
            Json.encodeToString(
                Demo_grade.getAverage(
                    statisticsReceiveRemote.eventId,
                    teamId
                )
            )
        )
    }

    suspend fun getComments() {
        val teamId = PersonTeam.getTeam(userId)
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "You must be on a team!")
            return
        }

        val statisticsReceiveRemote = call.receive<StatisticsReceiveRemote>()
        call.respond(
            Json.encodeToString(
                Demo_grade.getComments(
                    statisticsReceiveRemote.eventId,
                    teamId
                )
            )
        )
    }

    suspend fun getAllTeamStatistics() {
        if (!User.checkSuperUser(userId)){
            call.respond(HttpStatusCode.Forbidden, "You must have superuser rights!")
            return
        }

        val eventId = call.receive<StatisticsReceiveRemote>().eventId

        call.respond(Json.encodeToString(Demo_grade.getAverage(eventId)))
    }

    suspend fun getAllComments() {
        if (!User.checkSuperUser(userId)){
            call.respond(HttpStatusCode.Forbidden, "You must have superuser rights!")
            return
        }

        val eventId = call.receive<StatisticsReceiveRemote>().eventId

        call.respond(Json.encodeToString(Demo_grade.getComments(eventId)))
    }
}
