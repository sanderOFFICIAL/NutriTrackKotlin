package com.example.nutritrack.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nutritrack.screens.registration.user.ActivityLevelScreen
import com.example.nutritrack.screens.registration.user.CurrentWeightScreen
import com.example.nutritrack.screens.registration.user.DesiredWeightScreen
import com.example.nutritrack.screens.registration.user.GenderSelectionScreen
import com.example.nutritrack.screens.registration.user.HeightSelectionScreen
import com.example.nutritrack.screens.registration.user.YearSelectionScreen

fun NavGraphBuilder.userRegistrationNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "gender_selection_screen",
        route = "user_registration_graph"
    ) {
        composable("gender_selection_screen") {
            GenderSelectionScreen(
                onFemaleSelected = { navController.navigate("activity_level_screen") },
                onMaleSelected = { navController.navigate("activity_level_screen") }
            )
        }
        composable("activity_level_screen") {
            ActivityLevelScreen(
                onActivityLevelSelected = { level ->
                    navController.navigate("year_selection_screen")
                }
            )
        }
        composable("year_selection_screen") {
            YearSelectionScreen(
                onYearSelected = { year ->
                    // Зберігаємо рік, якщо потрібно
                },
                onNextClick = { navController.navigate("height_selection_screen") }
            )
        }
        composable("height_selection_screen") {
            HeightSelectionScreen(
                onHeightSelected = { height ->
                    navController.navigate("current_weight_screen")
                }
            )
        }
        composable("current_weight_screen") {
            CurrentWeightScreen(
                onWeightSelected = { weight ->
                    // Зберігаємо поточну вагу, якщо потрібно
                },
                onNextClick = { navController.navigate("desired_weight_screen") },
            )
        }
        composable("desired_weight_screen") {
            DesiredWeightScreen(
                onDesiredWeightSelected = { desiredWeight ->

                },
                onNextClick = {

                    navController.navigate("google_sign_in_screen/user")
                },
            )
        }
    }
}
