package com.ne_rabotaem.features.profile

import com.ne_rabotaem.database.person_team.Invite
import com.ne_rabotaem.database.person_team.InviteDTO
import com.ne_rabotaem.database.person_team.PersonTeam
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
            "father_name" to userDTO!!.father_name
        )))
    }

    suspend fun getTeam() {
        if (!isTokenValid)
            return

        val teamId = PersonTeam.getTeam(userId!!)
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "Your team not found!")
            return
        }

        val members = mutableMapOf<Int, String>()
        PersonTeam.getMembers(teamId).forEach {
            members[it] = User.fetch(it)!!.run { this.last_name + " " + this.first_name + " " + this.father_name }
        }

        call.respond(Json.encodeToString(members))
    }

    fun leave() {
        if (!isTokenValid)
            return

        PersonTeam.delete(userId!!)
    }

    suspend fun invite() {
        if (!isTokenValid)
            return

        val userIdRemote = call.receive<InviteReceiveRemote>()
        val teamId = PersonTeam.getTeam(userId!!)

        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "You must have a team!")
            return
        }

        Invite.insert(
            InviteDTO(
                teamId = teamId,
                fromWhom = userId!!,
                toWhom = userIdRemote.userId
            )
        )
    }

    suspend fun getInvites() {
        if (!isTokenValid)
            return

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
}
