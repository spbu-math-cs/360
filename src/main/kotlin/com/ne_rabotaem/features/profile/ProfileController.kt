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
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val first_name: String,
    val last_name: String,
    val father_name: String,
    val user_id: Int,
)

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
        println("SUCCCCCEEESSSSSSSSS!")

        val teamId = PersonTeam.getTeam(userId!!)
        println("teamId $teamId, personId $userId")
        if (teamId == null) {
            call.respond(HttpStatusCode.BadRequest, "Your team not found!")
            return
        }

        val members = mutableListOf<Member>()
        PersonTeam.getMembers(teamId).forEach {
            val user = User.fetch(it)!!;
            members += Member(user.first_name, user.last_name, user.father_name, it)
        }

        // call.respond(Json.encodeToString(mapOf<String, String>(
        //     "teamId" to teamId!!.toString(),
        //     "members" to Json.encodeToString(members)
        // )))

       call.respond(Json.encodeToString(teammatesResponseRemote(teamId!!, members)))
        // println(Json.encodeToString(mapOf<String, String>(
        //     "teamId" to teamId!!.toString(),
        //     "members" to Json.encodeToString(members)
        // )))
        // println(Json.encodeToString(TemmatesResponseRemote(teamId!!, members)))
    }

    suspend fun leave() {
        if (!isTokenValid)
            return

        PersonTeam.delete(userId!!)
        call.respond(HttpStatusCode.OK)
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
