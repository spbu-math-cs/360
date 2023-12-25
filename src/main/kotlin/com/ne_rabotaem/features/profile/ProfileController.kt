package com.ne_rabotaem.features.profile

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.event.EventType
import com.ne_rabotaem.database.grade.DemoGrade
import com.ne_rabotaem.database.person_team.InTeamGrade
import com.ne_rabotaem.database.person_team.Invite
import com.ne_rabotaem.database.person_team.InviteDTO
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.person_team.PersonTeamDTO
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.Hashing
import com.ne_rabotaem.utils.PasswordCheck
import com.ne_rabotaem.utils.UserCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class ProfileController(val call: ApplicationCall) {
    private val isUserValid get() = UserCheck.isUserExists(call)
    private val userId: Int
    private val imagePath = "img/user_images/"

    init {
        runBlocking {
            if (!isUserValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }

        userId = UserId.getId(call)!!
    }

    suspend fun getProfile() {
        val userDTO = User.fetch(Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login)
        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User was not found")
        }

        call.respond(MustacheContent("profile.hbs", mapOf<String, String>(
            "first_name" to userDTO!!.first_name,
            "last_name" to userDTO.last_name,
            "father_name" to userDTO.father_name,
            "user_id" to User.getUserId(userDTO.login)!!.toString().padStart(4, '0'),
            "profile_picture" to imagePath + (User.getImage(userId) ?: "default.jpg")
        )))
    }

    suspend fun getTeam() {
        val teamId = PersonTeam.getTeam(userId)
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
        if (PersonTeam.getTeam(userId) == null) {
            call.respond(HttpStatusCode.BadRequest, "You're not a member of any team!")
            return
        }

        PersonTeam.delete(userId)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun invite() {
        val invitedId = call.receive<InviteReceiveRemote>().UID.toInt()
        val teamId = PersonTeam.getTeam(userId)

        if (userId == invitedId) {
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
                fromWhom = userId,
                toWhom = invitedId
            )
        )

        call.respond(HttpStatusCode.OK)
    }

    suspend fun getInvites() {
        println(Json.encodeToString(
            Invite.getInvites(userId).map {
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
                Invite.getInvites(userId).map {
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
        Invite.delete(userId, teamId)

        if (ans.action == 0) {
            call.respond(HttpStatusCode.OK)
            return
        }

        if (PersonTeam.getTeam(userId) == null) {
            PersonTeam.insert(
                PersonTeamDTO(
                    personId = userId,
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
        val login = User.fetch(userId)!!.login

        if (PasswordCheck.isPasswordValid(userId, Hashing.getHash(login + passwordReceiveRemote.oldPassword))!!) {
            User.updatePassword(userId, Hashing.getHash(login + passwordReceiveRemote.newPassword))
            call.respond(HttpStatusCode.OK)    
            return
        }

        call.respond(HttpStatusCode.BadRequest, "Wrong password!")
    }

    suspend fun getInfo() {
        val login = Token.fetch(call.request.cookies.rawCookies["token"]!!)!!.login
        val userInfo = User.fetch(login)
        if (userInfo == null) {
            call.respond(HttpStatusCode.NotFound, "User not found!")
            return
        }

        call.respond(Json.encodeToString(
            UserInfoResponseRemote(
                userId,
                userInfo.rank.toString()
            )
        ))
    }

    suspend fun loadImage() {
        val data = call.receiveMultipart()

        val charPool : List<Char> = ('a'..'z') + ('0'..'9')
        var fileName: String = ThreadLocalRandom.current()
            .ints(10L, 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")

        data.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val originName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    fileName += "." + originName.split(".").last()
                    File("static/$imagePath/", fileName).writeBytes(fileBytes)
                }

                else -> {}
            }
            part.dispose()
        }

        val oldImgSrc = User.getImage(userId)
        if (oldImgSrc != null)
            File("static/$imagePath/$oldImgSrc").delete()
        User.addImage(userId, fileName)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun getUserDemoStatistics() {
        val teamId = PersonTeam.getTeam(userId)
        if (teamId == null) {
            call.respond(HttpStatusCode.NotFound, "Your team not found!")
            return
        }

        call.respond(Json.encodeToString(
            Event.fetchAll()
                .asSequence()
                .filter { it.type == EventType.demo }
                .associate { it.eventId to DemoGrade.getCalculatedAverage(it.eventId, teamId) * 
                                           InTeamGrade.getDemoUserRating(userId, it.eventId) / 
                                           InTeamGrade.getDemoAvgRating(teamId, it.eventId)
                }
                .toList()
        ))
    }
}
