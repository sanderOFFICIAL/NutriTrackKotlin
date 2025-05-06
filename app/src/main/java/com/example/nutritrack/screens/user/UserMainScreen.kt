package com.example.nutritrack.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.GoalResponse
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(
    onLogoutClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var goalData by remember { mutableStateOf<GoalResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var streak by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    fun saveLastLoginDate(date: String) {
        sharedPreferences.edit().putString("lastLoginDate", date).apply()
    }

    fun getLastLoginDate(): String? {
        return sharedPreferences.getString("lastLoginDate", null)
    }

    fun saveStreak(value: Int) {
        sharedPreferences.edit().putInt("currentStreak", value).apply()
    }

    fun getStreak(): Int {
        return sharedPreferences.getInt("currentStreak", 0)
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null

                val idToken = FirebaseAuthHelper.getIdToken()
                if (idToken == null) {
                    errorMessage = "Authorization error: IdToken not found"
                    return@launch
                }

                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val lastLoginDateStr = getLastLoginDate()
                var currentStreak = getStreak()

                if (lastLoginDateStr == null) {
                    currentStreak = 1
                    val success = ApiService.addStreak(idToken, currentStreak)
                    if (success) {
                        saveStreak(currentStreak)
                        saveLastLoginDate(currentDate)
                    }
                } else {
                    val lastLoginDate =
                        LocalDate.parse(lastLoginDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    val daysDifference =
                        ChronoUnit.DAYS.between(lastLoginDate, LocalDate.now()).toInt()

                    when {
                        daysDifference == 0 -> {
                            currentStreak = getStreak()
                        }

                        daysDifference == 1 -> {
                            currentStreak = getStreak() + 1
                            val success = ApiService.updateStreak(idToken, currentStreak, true)
                            if (success) {
                                saveStreak(currentStreak)
                                saveLastLoginDate(currentDate)
                            }
                        }

                        daysDifference >= 2 -> {
                            currentStreak = 0
                            val success = ApiService.updateStreak(idToken, currentStreak, false)
                            if (success) {
                                saveStreak(currentStreak)
                                saveLastLoginDate(currentDate)
                            }
                        }
                    }
                }
                streak = currentStreak

                val goalIds = ApiService.getAllUserGoalIds(idToken)
                if (goalIds.isEmpty()) {
                    errorMessage = "Target not found"
                    return@launch
                }

                val goalId = goalIds.first().goalId

                val goal = ApiService.getSpecificGoalById(goalId)
                if (goal == null) {
                    errorMessage = "Unable to obtain target details"
                    return@launch
                }
                goalData = goal
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = "Logout",
                                modifier = Modifier.size(35.dp),
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "NutriTrack",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Text(
                        text = streak.toString(),
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = { /* TODO: Дія для іконки вогника */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flame),
                            contentDescription = "Fire",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* TODO: Дія для календаря */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Calendar",
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
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2F4F4F),
                modifier = Modifier.height(102.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* TODO: Дія для "Записник" */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notebook),
                            contentDescription = "Notebook",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = "Notebook",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF64A79B)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: Дія для "Активність" */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_activity),
                            contentDescription = "Activity",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = "Activity",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF64A79B)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onProfileClick()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF64A79B)
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF64A79B))
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else if (goalData != null) {
                Text(
                    text = "Today.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = "The number of calories",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0f },
                        modifier = Modifier
                            .size(175.dp)
                            .clip(CircleShape),
                        color = Color.White.copy(alpha = 0.3f),
                        strokeWidth = 11.dp,
                        trackColor = Color.White
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = goalData!!.dailyCalories.toInt().toString(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Consumed: 0\nBurned: 0",
                            fontSize = 14.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier
                                .width(80.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "Proteins\n0/${goalData!!.dailyProtein.toInt()}",
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier
                                .width(80.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "Fats\n0/${goalData!!.dailyFats.toInt()}",
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier
                                .width(80.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "Carbs\n0/${goalData!!.dailyCarbs.toInt()}",
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Today's diet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                val totalCalories = goalData!!.dailyCalories
                val breakfastCalories = (totalCalories * 0.3).toInt() // 30%
                val lunchCalories = (totalCalories * 0.35).toInt()    // 35%
                val dinnerCalories = (totalCalories * 0.25).toInt()    // 25%
                val snackCalories = (totalCalories * 0.1).toInt()      // 10%

                val meals = listOf(
                    Triple("Breakfast", R.drawable.ic_breakfast, breakfastCalories),
                    Triple("Lunch", R.drawable.ic_lunch, lunchCalories),
                    Triple("Dinner", R.drawable.ic_dinner, dinnerCalories),
                    Triple("Snack", R.drawable.ic_snack, snackCalories)
                )
                meals.forEach { (meal, iconRes, calories) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2F4F4F)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = meal,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = meal,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "0/$calories cal",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(onClick = { /* TODO: Дія для додавання їжі */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "Add",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}