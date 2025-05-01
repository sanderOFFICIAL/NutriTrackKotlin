package com.example.nutritrack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nutritrack.data.consultant.ConsultantRegistrationViewModel
import com.example.nutritrack.data.user.UserGoalViewModel
import com.example.nutritrack.data.user.UserRegistrationViewModel
import com.example.nutritrack.screens.WelcomeScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val consultantViewModel: ConsultantRegistrationViewModel = viewModel()
    val userViewModel: UserRegistrationViewModel = viewModel()
    val userGoalViewModel: UserGoalViewModel = viewModel()
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
        userRegistrationNavGraph(navController, userViewModel, userGoalViewModel)
        consultantRegistrationNavGraph(navController, consultantViewModel)
    }
}