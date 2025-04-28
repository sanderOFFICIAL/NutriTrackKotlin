package com.example.nutritrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nutritrack.screens.WelcomeScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome_screen"
    ) {
        composable("welcome_screen") {
            WelcomeScreen(
                onNewUserClick = {
                    navController.navigate("user_registration_graph")
                },
                onExistingUserClick = { /* TODO: Додати перехід для "Вже маю акаунт" */ },
                onConsultantClick = {
                    navController.navigate("consultant_registration_graph")
                }
            )
        }
        // Додаємо вкладені графи на верхній рівень
        userRegistrationNavGraph(navController)
        consultantRegistrationNavGraph(navController)
    }
}