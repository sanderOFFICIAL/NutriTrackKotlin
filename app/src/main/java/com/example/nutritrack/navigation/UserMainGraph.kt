package com.example.nutritrack.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nutritrack.screens.UserMainScreen
import com.example.nutritrack.screens.user.UserProfileScreen

fun NavGraphBuilder.userMainNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = "user_main_screen",
        route = "user_main_graph"
    ) {
        composable("user_main_screen") {
            UserMainScreen(
                onLogoutClick = {
                    navController.navigate("welcome_screen") {
                        popUpTo("user_main_graph") { inclusive = true }
                    }
                },
                onProfileClick = {
                    navController.navigate("user_profile_screen")
                }
            )
        }
        composable("user_profile_screen") {
            UserProfileScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}