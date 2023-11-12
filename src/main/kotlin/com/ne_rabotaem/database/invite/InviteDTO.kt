package com.ne_rabotaem.database.person_team

class InviteDTO(
    val teamId: Int,
    val toWhom: Int,
    val fromWhom: Int
)

class InviteIdDTO(
    val id: Int,
    val teamId: Int,
    val toWhom: Int,
    val fromWhom: Int
)