package com.example.nutritrack.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.nutritrack.screens.user.DatePickerScreen
import com.example.nutritrack.screens.user.FoodSearchScreen
import com.example.nutritrack.screens.user.HistoryScreen
import com.example.nutritrack.screens.user.MealDetailsScreen
import com.example.nutritrack.screens.user.UserMainScreen
import com.example.nutritrack.screens.user.UserProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
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
                },
                onAddFoodClick = { mealType ->
                    navController.navigate("food_search_screen/$mealType")
                },
                onViewMealDetails = { mealType ->
                    navController.navigate("meal_details_screen/$mealType")
                },
                onCalendarClick = {
                    navController.navigate("date_picker_screen")
                }
            )
        }

        composable(
            "meal_details_screen/{mealType}",
            arguments = listOf(navArgument("mealType") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
            MealDetailsScreen(
                mealType = mealType,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("user_profile_screen") {
            UserProfileScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSuccessScreenClick = {
                    navController.navigate("user_success_screen")
                },
                onNotebookClick = {
                    navController.navigate("user_main_screen") {
                        popUpTo("user_main_graph") { inclusive = true }
                    }
                },
                onActivityClick = {

                }
            )
        }
    }
    composable("food_search_screen/{mealType}") { backStackEntry ->
        val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
        FoodSearchScreen(
            mealType = mealType,
            onBackClick = {
                navController.navigateUp()
            },
            onFoodAdded = { consumedFood ->
                // TODO: Save consumedFood to backend or local storage
                // For now, we'll just navigate back
                navController.navigateUp()
            }
        )
    }
    composable("date_picker_screen") {
        DatePickerScreen(
            onDateSelected = { selectedDate ->
                navController.navigate("history_screen/$selectedDate") {
                    popUpTo("date_picker_screen") {
                        inclusive = true
                    } // Закриваємо DatePickerScreen
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    // Додаємо екран історії за вибраний день
    composable(
        "history_screen/{selectedDate}",
        arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
    ) { backStackEntry ->
        val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
        HistoryScreen(
            selectedDate = selectedDate,
            onBackClick = { navController.popBackStack() },
            onAddFoodClick = { mealType ->
                navController.navigate("food_search_screen/$mealType")
            },
            onViewMealDetails = { mealType ->
                navController.navigate("meal_details_screen/$mealType")
            }
        )
    }
}
