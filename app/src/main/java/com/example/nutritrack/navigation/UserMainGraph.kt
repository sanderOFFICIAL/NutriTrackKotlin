package com.example.nutritrack.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nutritrack.screens.user.FoodSearchScreen
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
                }
            )
        }
        composable("user_profile_screen") {
            UserProfileScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onSuccessScreenClick = {
                    navController.navigate("user_success_screen")
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
}
