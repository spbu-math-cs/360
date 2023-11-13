package com.ne_rabotaem.features.profile

import com.ne_rabotaem.database.person_team.Invite
import com.ne_rabotaem.database.person_team.InviteDTO
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.person_team.PersonTeamDTO
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.PasswordCheck
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

    suspend fun getProfile() {
        val userDTO = User.fetch(Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login)
        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User was not found")
        }

        call.respond(MustacheContent("profile.hbs", mapOf<String, String>(
            "first_name" to userDTO!!.first_name,
            "last_name" to userDTO!!.last_name,
            "father_name" to userDTO!!.father_name,
            "user_id" to User.getUserId(userDTO!!.login)!!.toString().padStart(4, '0')
        )))
    }

    suspend fun getTeam() {
        val teamId = PersonTeam.getTeam(userId!!)
        println("teamId $teamId, personId $userId")
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "Your team was not found!")
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

       call.respond(Json.encodeToString(TeammatesResponseRemote(teamId!!, members)))
    }

    suspend fun leave() {
        if (PersonTeam.getTeam(userId!!) == null) {
            call.respond(HttpStatusCode.BadRequest, "You're not a member of any team!")
            return
        }

        call.respond(HttpStatusCode.OK)
        PersonTeam.delete(userId!!)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun invite() {
        val invitedId = call.receive<InviteReceiveRemote>().UID.toInt()
        val teamId = PersonTeam.getTeam(userId!!)

        if (userId!! == invitedId) {
            call.respond(HttpStatusCode(403, "Self-invitation"), "You can not invite yourself!")
        }

        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "You must be a member of the team!")
            return
        }

        if (PersonTeam.getTeam(invitedId) != null) {
            call.respond(HttpStatusCode.Conflict, "This user is already a member of the team!")
            return
        }

        if (Invite.haveInvite(teamId, invitedId)) {
            call.respond(HttpStatusCode(402, "Double invite"), "This user has already been invited to your team!")
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

        if (ans.action == 0) {
            call.respond(HttpStatusCode.OK)
            return
        }

        if (PersonTeam.getTeam(userId!!) == null) {
            PersonTeam.insert(
                PersonTeamDTO(
                    personId = userId!!,
                    teamId = teamId
                )
            )
            call.respond(HttpStatusCode.OK)
            return
        }

        call.respond(HttpStatusCode.BadRequest, "You're already a member of the team!")
    }

    suspend fun changePassword() {
        val passwordReceiveRemote = call.receive<NewPasswordReceiveRemote>()

        if (PasswordCheck.isPasswordValid(userId!!, passwordReceiveRemote.oldPassword)!!) {
            User.updatePassword(userId!!, passwordReceiveRemote.newPassword)
            call.respond(HttpStatusCode.OK)    
            return
        }

        call.respond(HttpStatusCode.BadRequest, "Wrong password!")
    }
}
