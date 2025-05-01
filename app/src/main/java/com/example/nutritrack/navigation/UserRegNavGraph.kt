package com.example.nutritrack.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nutritrack.data.user.UserRegistrationViewModel
import com.example.nutritrack.screens.UserSuccessScreen
import com.example.nutritrack.screens.registration.user.ActivityLevelScreen
import com.example.nutritrack.screens.registration.user.CurrentWeightScreen
import com.example.nutritrack.screens.registration.user.GenderSelectionScreen
import com.example.nutritrack.screens.registration.user.HeightSelectionScreen
import com.example.nutritrack.screens.registration.user.UserNicknameScreen
import com.example.nutritrack.screens.registration.user.YearSelectionScreen

fun NavGraphBuilder.userRegistrationNavGraph(
    navController: NavHostController,
    viewModel: UserRegistrationViewModel
) {
    navigation(
        startDestination = "gender_selection_screen",
        route = "user_registration_graph"
    ) {
        composable("gender_selection_screen") {
            GenderSelectionScreen(
                viewModel = viewModel,
                onNextClick = {
                    navController.navigate("activity_level_screen")
                }
            )
        }
        composable("activity_level_screen") {
            ActivityLevelScreen(
                viewModel = viewModel,
                onNextClick = {
                    navController.navigate("year_selection_screen")
                }
            )
        }
        composable("year_selection_screen") {
            YearSelectionScreen(
                viewModel = viewModel,
                onNextClick = { navController.navigate("height_selection_screen") }
            )
        }
        composable("height_selection_screen") {
            HeightSelectionScreen(
                viewModel = viewModel,
                onNextClick = {
                    navController.navigate("current_weight_screen")
                }
            )
        }
        composable("current_weight_screen") {
            CurrentWeightScreen(
                viewModel = viewModel,
                onNextClick = { navController.navigate("user_nickname_screen") },
            )
        }
        composable("user_nickname_screen") {
            UserNicknameScreen(
                viewModel = viewModel,
                onRegistrationSuccess = {
                    navController.navigate("user_success_screen")
                },
            )
        }
        composable("user_success_screen") {
            UserSuccessScreen(
                onNavigateToMainScreen = {
                    navController.navigate("user_main_screen") {
                        popUpTo("user_registration_graph") { inclusive = true }
                    }
                }
            )
        }
    }
}
