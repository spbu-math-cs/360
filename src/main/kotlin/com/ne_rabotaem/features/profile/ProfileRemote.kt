package com.ne_rabotaem.features.profile

import kotlinx.serialization.Serializable

@Serializable
data class InviteReceiveRemote(
    val UID: String
)

@Serializable
data class InviteResponceRemote(
    val inviteId: Int,
    val teamNum: Int,
    val inviterFirstName: String,
    val inviterLastName: String,
    val inviterUserId: Int
)

@Serializable
data class TeammatesResponseRemote(
    val teamId: Int,
    val members: List<TeammateResponseRemote>
)
@Serializable
data class TeammateResponseRemote(
    val user_id: Int,
    val first_name: String,
    val last_name: String,
    val father_name: String
)

@Serializable
data class InviteAnswerReceiveRemote(
    val inviteId: Int,
    val action: Int
)