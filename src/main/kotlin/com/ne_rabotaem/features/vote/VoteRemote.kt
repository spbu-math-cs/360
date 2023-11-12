package com.ne_rabotaem.features.vote

import kotlinx.serialization.Serializable

@Serializable
data class InteamGradeReceiveRemote(
    val eventId: Int,
    val personId: Int,
    val grades: List<Int>
)