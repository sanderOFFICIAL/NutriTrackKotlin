package com.example.nutritrack.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserGoalData(
    val idToken: String = "",
    val goal_type: Int = 0, // 0 - підтримувати, 1 - зменшити, 2 - набрати
    val target_weight: Int = 0,
    val duration_weeks: Int = 0
)