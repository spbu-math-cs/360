package com.ne_rabotaem.features.profile

import com.ne_rabotaem.database.person_team.Invite
import com.ne_rabotaem.database.person_team.InviteDTO
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.person_team.PersonTeamDTO
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.token.Token
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

class ProfileController(val call: ApplicationCall) {
    private val isTokenValid: Boolean = TokenCheck.isTokenValid(call)
    private var userId: Int? = UserId.getId(call)

    init {
        runBlocking {
            if (!isTokenValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }
    }

    suspend fun getProfile() {
        if (!isTokenValid)
            return

        val userDTO = User.fetch(Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login)
        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        }

        call.respond(MustacheContent("profile.hbs", mapOf<String, String>(
            "first_name" to userDTO!!.first_name,
            "last_name" to userDTO!!.last_name,
            "father_name" to userDTO!!.father_name,
            "user_id" to User.getUserId(userDTO!!.login)!!.toString().padStart(4, '0')
        )))
    }

    suspend fun getTeam() {
        if (!isTokenValid)
            return

        val teamId = PersonTeam.getTeam(userId!!)
        println("teamId $teamId, personId $userId")
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "Your team not found!")
            return
        }

        val members: List<TeammateResponseRemote> = PersonTeam.getMembers(teamId).map {
            User.fetch(it)!!.run {
                TeammateResponseRemote(
                    user_id = it,
                    first_name = this.first_name,
                    last_name = this.last_name,
                    father_name = this.father_name
                )
            }
        }

        // call.respond(Json.encodeToString(mapOf<String, String>(
        //     "teamId" to teamId!!.toString(),
        //     "members" to Json.encodeToString(members)
        // )))

       call.respond(Json.encodeToString(TeammatesResponseRemote(teamId!!, members)))
        // println(Json.encodeToString(mapOf<String, String>(
        //     "teamId" to teamId!!.toString(),
        //     "members" to Json.encodeToString(members)
        // )))
        // println(Json.encodeToString(TemmatesResponseRemote(teamId!!, members)))
    }

    suspend fun leave() {
        if (!isTokenValid)
            return

        if (PersonTeam.getTeam(userId!!) == null) {
            call.respond(HttpStatusCode.BadRequest, "You have not team!")
            return
        }

        PersonTeam.delete(userId!!)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun invite() {
        if (!isTokenValid)
            return

        val invitedId = call.receive<InviteReceiveRemote>().UID.toInt()
        val teamId = PersonTeam.getTeam(userId!!)

        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "You must have a team!")
            return
        }

        if (PersonTeam.getTeam(invitedId) != null) {
            call.respond(HttpStatusCode.Conflict, "Invited man already on the team!")
            return
        }

        if (Invite.haveInvite(teamId, invitedId)) {
            call.respond(HttpStatusCode.BadRequest, "This man has already been invited!")
            return
        }

        Invite.insert(
            InviteDTO(
                teamId = teamId,
                fromWhom = userId!!,
                toWhom = invitedId
            )
        )

        call.respond(HttpStatusCode.OK)
    }

    suspend fun getInvites() {
        if (!isTokenValid)
            return

        println(Json.encodeToString(
            Invite.getInvites(userId!!).map {
                val userDTO = User.fetch(it.fromWhom)!!
                InviteResponceRemote(
                    inviteId = it.id,
                    teamNum = Team.fetch(it.teamId)!!.number,
                    inviterFirstName = userDTO.first_name,
                    inviterLastName = userDTO.last_name,
                    inviterUserId = it.fromWhom
                )
            }
        ))
        call.respond(
            Json.encodeToString(
                Invite.getInvites(userId!!).map {
                    val userDTO = User.fetch(it.fromWhom)!!
                    InviteResponceRemote(
                        inviteId = it.id,
                        teamNum = Team.fetch(it.teamId)!!.number,
                        inviterFirstName = userDTO.first_name,
                        inviterLastName = userDTO.last_name,
                        inviterUserId = it.fromWhom
                    )
                }
            )
        )
    }

    suspend fun answer() {
        val ans = call.receive<InviteAnswerReceiveRemote>()

        val teamId = Invite.fetch(ans.inviteId)!!.teamId
        Invite.delete(userId!!, teamId)

        if (ans.action == 0)
            return

        if (PersonTeam.getTeam(userId!!) == null) {
            PersonTeam.insert(
                PersonTeamDTO(
                    personId = userId!!,
                    teamId = teamId
                )
            )
            return
        }

        call.respond(HttpStatusCode.BadRequest, "You already have a team!")
    }
}
