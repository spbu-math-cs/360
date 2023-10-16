package com.ne_rabotaem.database.user

import kotlinx.serialization.Serializable


@Serializable
data class UserDTO(
    val first_name: String,
    val last_name: String,
    val father_name: String,
    val login: String,
    val password: String,
    val rank: rank
)