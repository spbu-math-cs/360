package com.ne_rabotaem.features.demo.statistics

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponseRemote(
    val avgLevel: Float,
    val avgGrade: Float,
    val avgPresentation: Float,
    val avgAdditional: Float
)

@Serializable
data class StatisticsParameters(
    val eventId: Int,
    val teamId: Int
)

@Serializable
data class CommentReceiveRemote(
    val firstName: String,
    val lastName: String,
    val fatherName: String,
    val comment: String
)