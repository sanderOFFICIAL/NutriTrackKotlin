package com.example.nutritrack.screens.consultant

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.model.GoalResponse
import com.example.nutritrack.model.MealEntry
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserMealScreen(
    userUid: String,
    onBackClick: () -> Unit
) {
    var meals by remember { mutableStateOf<List<MealEntry>>(emptyList()) }
    var groupedMeals by remember {
        mutableStateOf<Map<Pair<String, String>, List<MealEntry>>>(
            emptyMap()
        )
    }
    var goal by remember { mutableStateOf<GoalResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentDate = LocalDate.now()

    LaunchedEffect(userUid) {
        try {
            isLoading = true
            errorMessage = null

            val goalIdResponse = ApiService.getGoalIdByUserUid(userUid)
            if (goalIdResponse != null) {
                val goalResponse = ApiService.getSpecificGoalById(goalIdResponse.goalId)
                goal = goalResponse
            } else {
                errorMessage = "No goal found for this user."
            }

            meals = ApiService.getMealsByUid(userUid)
            if (meals.isEmpty()) {
                errorMessage = if (errorMessage != null) {
                    "$errorMessage\nNo meal data available for this user."
                } else {
                    "No meal data available for this user."
                }
            } else {
                val filteredMeals = meals.filter {
                    val mealDate = LocalDate.parse(
                        it.entry_date.split("T")[0],
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                    mealDate == currentDate
                }
                if (filteredMeals.isEmpty()) {
                    errorMessage = if (errorMessage != null) {
                        "$errorMessage\nNo meal data available for today."
                    } else {
                        "No meal data available for today."
                    }
                } else {
                    groupedMeals = filteredMeals.groupBy { Pair(it.entry_date, it.meal_type) }
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error loading data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Meal History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(35.dp),
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2F4F4F)
            ),
            modifier = Modifier.height(75.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                if (goal != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "User's Daily Goal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Calories: ${goal!!.dailyCalories} kcal",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Protein: ${goal!!.dailyProtein} g",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Carbs: ${goal!!.dailyCarbs} g",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Fats: ${goal!!.dailyFats} g",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groupedMeals.entries.toList()) { (key, mealGroup) ->
                        val (date, mealType) = key
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Meal Type: ${mealType.replaceFirstChar { it.uppercase() }}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Date: ${formatDate(date)}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Created: ${formatTime(mealGroup.first().created_at)}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                mealGroup.forEach { meal ->
                                    Text(
                                        text = "• ${meal.product_name} (${meal.quantity_grams} g)",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                                val totalCalories = mealGroup.sumOf { it.calories }
                                val totalProtein = mealGroup.sumOf { it.protein }
                                val totalCarbs = mealGroup.sumOf { it.carbs }
                                val totalFats = mealGroup.sumOf { it.fats }
                                Text(
                                    text = "Total Calories: $totalCalories kcal",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Total Protein: $totalProtein g",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Total Carbs: $totalCarbs g",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Total Fats: $totalFats g",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateTime = LocalDateTime.parse(dateStr, formatter)
        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime)
    } catch (e: Exception) {
        dateStr
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(timeStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateTime = LocalDateTime.parse(timeStr, formatter)
        DateTimeFormatter.ofPattern("HH:mm").format(dateTime)
    } catch (e: Exception) {
        timeStr
    }
}