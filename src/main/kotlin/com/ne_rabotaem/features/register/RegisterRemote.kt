package com.ne_rabotaem.features.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote(
    val first_name: String,
    val last_name: String,
    val father_name: String,
    val login: String,
    val password: String,
    val rank: String
)
