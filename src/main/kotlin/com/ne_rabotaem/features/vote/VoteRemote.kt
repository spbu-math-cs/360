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

@Serializable
data class PersonDemoGradeResponseRemote(
    val teamId: Int,
    val level: Int,
    val grade: Int,
    val presentation: Int,
    val additional: Int,
    val comment: String
)