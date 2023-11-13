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
import com.ne_rabotaem.features.demo.GradeReceiveRemote
import com.ne_rabotaem.utils.TokenCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import java.time.LocalTime

class VoteController(val call: ApplicationCall) {
    private val isTokenValid get() = TokenCheck.isTokenValid(call)
    private var userId: Int? = UserId.getId(call)

    init {
        runBlocking {
            if (!isTokenValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }
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

    suspend fun getTeams(eventId: Int) {
        val userId = User.getUserId(
            Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login
        )!!

        val userTeam = PersonTeam.getTeam(userId!!);
        if (userTeam == null) {
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
        if (grade.grade < 0 || grade.grade > 10) {
            call.respond(HttpStatusCode.PreconditionFailed, "Grade must be in range from 0 to 10!")
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
    }
}