package com.ne_rabotaem.database.grade

class GradeDTO(
    val eventId: Int,
    val personId: Int,
    val teamId: Int,
    val level: Int,
    val grade: Int,
    val presentation: Int,
    val additional: Int,
    val comment: String?
)