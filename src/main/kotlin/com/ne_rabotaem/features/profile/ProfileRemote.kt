package com.ne_rabotaem.features.profile

import com.ne_rabotaem.features.demo.TeamResponseRemote
import kotlinx.serialization.Serializable
import java.util.Dictionary

@Serializable
data class InviteReceiveRemote(
    val userId: Int
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
data class temmatesResponseRemote(
    val id: Int,
    val members: List<teammateResponseRemote>
)
@Serializable
data class teammateResponseRemote(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val fatherName: String
)