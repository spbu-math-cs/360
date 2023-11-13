package com.ne_rabotaem.features.vote

import kotlinx.serialization.Serializable

@Serializable
data class PersonGrade(
    val personId: Int,
    val grade: Int
)

@Serializable
data class InteamGradeReceiveRemote(
    val eventId: Int,
    val grades: List<PersonGrade>
)