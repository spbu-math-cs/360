package com.ne_rabotaem.features.vote

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.event.EventDTO
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.grade.GradeDTO
import com.ne_rabotaem.database.person_team.InteamGrade
import com.ne_rabotaem.database.person_team.Invite
import com.ne_rabotaem.database.person_team.InteamGradeDTO
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.person_team.PersonTeamDTO
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.database.user.User.checkSuperUser
import com.ne_rabotaem.features.demo.GradeReceiveRemote
import com.ne_rabotaem.utils.TokenCheck
import com.ne_rabotaem.utils.UserCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalTime

class VoteController(val call: ApplicationCall) {
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

    fun isDemoValid(eventDTO: EventDTO): Boolean {
        return eventDTO.start < LocalTime.now() && eventDTO.finish > LocalTime.now()
    }

    suspend fun getPage(eventId: Int) {
        var eventDTO = Event.fetch(eventId)
        if (eventDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "No such event!")
            return;
        }

        if (!isDemoValid(eventDTO)) {
            call.respond(HttpStatusCode.Locked, "You can only vote during demo!")
            return
        }

        call.respond(MustacheContent("voting.html", mapOf<String, String>()))
    }

    suspend fun getDemo(id: Int) {
        call.respond(Demo_grade.fetch(id).groupBy { it.teamId })
    }

    suspend fun getTeams() {
        if (!call.request.queryParameters.contains("eventId")) {
            call.respond(HttpStatusCode.BadRequest, "Request must contain eventId!")
            return
        }

        val eventId = Integer.parseInt(call.request.queryParameters["eventId"]!!)

        val userId = User.getUserId(
            Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login
        )!!

        val userTeam = PersonTeam.getTeam(userId!!);
        if (userTeam == null && !checkSuperUser(userId)) {
            call.respond(HttpStatusCode.Conflict, "You're not on any of the teams")
            return
        }

        var eventDTO = Event.fetch(eventId)
        if (eventDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "No such event!")
            return;
        }

        if (!isDemoValid(eventDTO)) {
            call.respond(HttpStatusCode.Locked, "You can only vote during demo!")
            return
        }

        call.respond(Team.fetchAll().filter {it.teamId != userTeam});
    }

    suspend fun vote() {
        val grade = call.receive<GradeReceiveRemote>()
        if (grade.comment.length > 500) {
            call.respond(HttpStatusCode.PayloadTooLarge, "Comment length must be less than 500 symbols!")
            return
        }
        if (grade.grade < 1 || grade.grade > 5 || grade.level < 1 || grade.level > 5 ||
            grade.presentation < 1 || grade.presentation > 5 || grade.additional < 0 || grade.additional > 3) {
            call.respond(HttpStatusCode.PreconditionFailed, "Grade must be in range from 1 to 5!")
            return
        }

        var eventDTO = Event.fetch(grade.eventId)
        if (eventDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "No such event!")
            return;
        }
        if (!isDemoValid(eventDTO)) {
            call.respond(HttpStatusCode.Locked, "You can only vote during demo!")
            return
        }

        val userId = User.getUserId(
            Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login
        )!!

        var gradeId = Demo_grade.getId(grade.eventId, userId, grade.teamId);
        if (gradeId != null) {
            Demo_grade.update(
                gradeId,
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

    suspend fun inteamVote() {
        val inteamGradeReceiveRemote = call.receive<InteamGradeReceiveRemote>()

        inteamGradeReceiveRemote.grades.forEach {
            if (userId!! != it.personId) {
                InteamGrade.insertOrUpdate(
                    InteamGradeDTO(
                        eventId = inteamGradeReceiveRemote.eventId,
                        evaluatorId = userId!!,
                        assessedId = it.personId,
                        grade = it.grade
                    )
                )
            }
        }

        call.respond(HttpStatusCode.OK)
    }

    suspend fun getDemoGrades() {
        val eventId = call.parameters["eventId"]?.toInt()

        if (eventId == null) {
            call.respond(HttpStatusCode.BadRequest, "Request must contain int eventId!")
            return
        }

        val userId = UserId.getId(call)!!
        val grades = Demo_grade.getGrades(userId, eventId)

        call.respond(Json.encodeToString(grades))
    }
}