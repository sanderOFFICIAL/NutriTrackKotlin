package com.example.nutritrack.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nutritrack.screens.registration.consultant.ConsultantExperienceYearsScreen
import com.example.nutritrack.screens.registration.consultant.ConsultantGenderSelectionScreen
import com.example.nutritrack.screens.registration.consultant.ConsultantMaxClientsScreen
import com.example.nutritrack.screens.registration.consultant.ConsultantNicknameScreen
import com.example.nutritrack.screens.registration.consultant.ConsultantProfileDescriptionScreen
import com.example.nutritrack.screens.registration.consultant.ConsultantProfilePictureScreen

fun NavGraphBuilder.consultantRegistrationNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "consultant_gender_selection_screen",
        route = "consultant_registration_graph"
    ) {
        composable("consultant_gender_selection_screen") {
            ConsultantGenderSelectionScreen(
                onFemaleSelected = {
                    // Зберігаємо стать "Жінка", якщо потрібно
                },
                onMaleSelected = {
                    // Зберігаємо стать "Чоловік", якщо потрібно
                },
                onNextClick = {
                    navController.navigate("consultant_nickname_screen")
                }
            )
        }
        composable("consultant_nickname_screen") {
            ConsultantNicknameScreen(
                onNicknameSelected = { nickname ->
                    // Зберігаємо псевдонім, якщо потрібно
                },
                onNextClick = {
                    navController.navigate("consultant_profile_picture_screen")
                }
            )
        }

        composable("consultant_profile_picture_screen") {
            ConsultantProfilePictureScreen(
                onProfilePictureSelected = { picture ->
                    // Зберігаємо посилання на фото, якщо потрібно
                },
                onNextClick = {
                    navController.navigate("consultant_profile_description_screen")
                }
            )
        }
        composable("consultant_profile_description_screen") {
            ConsultantProfileDescriptionScreen(
                onProfileDescriptionSelected = { description ->
                    // Зберігаємо опис, якщо потрібно
                },
                onNextClick = {
                    navController.navigate("consultant_experience_years_screen")
                }
            )
        }
        composable("consultant_experience_years_screen") {
            ConsultantExperienceYearsScreen(
                onExperienceYearsSelected = { years ->
                    // Зберігаємо досвід, якщо потрібно
                },
                onNextClick = {
                    navController.navigate("consultant_max_clients_screen")
                }
            )
        }
        composable("consultant_max_clients_screen") {
            ConsultantMaxClientsScreen(
                onMaxClientsSelected = { maxClients ->
                    // Зберігаємо максимальну кількість клієнтів, якщо потрібно
                },
                onNextClick = {
                    navController.navigate("")
                }
            )
        }
    }
}