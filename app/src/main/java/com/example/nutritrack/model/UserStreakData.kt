package com.example.nutritrack.model

import kotlinx.serialization.Serializable

@Serializable
data class AddStreakRequest(
    val idToken: String,
    val current_streak: Int
)

@Serializable
data class UpdateStreakRequest(
    val idToken: String,
    val current_streak: Int,
    val is_active: Boolean
)

@Serializable
data class StreakResponse(
    val currentStreak: Int,
    val isActive: Boolean
)