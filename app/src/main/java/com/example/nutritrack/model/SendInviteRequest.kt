package com.example.nutritrack.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSendInviteRequest(
    val idToken: String,
    val consultant_uid: String
)