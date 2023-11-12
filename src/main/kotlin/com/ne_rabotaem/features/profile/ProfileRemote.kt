package com.ne_rabotaem.features.profile

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