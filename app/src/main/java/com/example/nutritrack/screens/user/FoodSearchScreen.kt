package com.example.nutritrack.screens.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.api.FoodItem
import com.example.nutritrack.data.api.UsdaFoodDataApi
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.AddMealRequest
import com.example.nutritrack.model.ProductData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    mealType: String,
    onBackClick: () -> Unit,
    onFoodAdded: (FoodItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var foodItems by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFoodId by remember { mutableStateOf<String?>(null) }
    var detailedFood by remember { mutableStateOf<FoodItem?>(null) }
    var isLoadingDetails by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var debouncedQuery by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val usdaFoodDataApi =
        remember { UsdaFoodDataApi(apiKey = "qNfgVVe13Ywbdl3hJRTPjTTwHaxsZi5x2beIZXM4") }

    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300)
            if (searchQuery != debouncedQuery) {
                debouncedQuery = searchQuery
            }
        }
    }

    LaunchedEffect(debouncedQuery) {
        if (debouncedQuery.length >= 2) {
            isLoading = true
            errorMessage = null
            Log.d("FoodSearchScreen", "Performing search for query: $debouncedQuery")
            foodItems = usdaFoodDataApi.searchFood(debouncedQuery, maxResults = 20)
            Log.d("FoodSearchScreen", "Received ${foodItems.size} items")
            if (foodItems.isEmpty()) {
                errorMessage = "No items found. Try a different search term."
            }
            isLoading = false
        } else {
            foodItems = emptyList()
            errorMessage = null
        }
    }

    LaunchedEffect(selectedFoodId) {
        if (selectedFoodId != null) {
            Log.d("FoodSearchScreen", "Fetching details for fdcId: $selectedFoodId")
            isLoadingDetails = true
            showErrorDialog = false
            detailedFood = usdaFoodDataApi.getFoodDetails(selectedFoodId!!)
            Log.d("FoodSearchScreen", "Received details: $detailedFood")
            isLoadingDetails = false

            if (detailedFood == null) {
                showErrorDialog = true
            }
        } else {
            detailedFood = null
            showErrorDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Add to $mealType",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            placeholder = { Text("Search for food...", color = Color.White.copy(alpha = 0.7f)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2F4F4F),
                unfocusedContainerColor = Color(0xFF2F4F4F),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                if (searchQuery.length >= 2) {
                    debouncedQuery = searchQuery
                }
            }),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Start
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }

                searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Enter a food name to search",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(foodItems) { foodItem ->
                            FoodItemCard(
                                foodItem = foodItem,
                                onClick = {
                                    Log.d(
                                        "FoodSearchScreen",
                                        "Clicked on item: ${foodItem.name}, fdcId: ${foodItem.id}"
                                    )
                                    selectedFoodId = foodItem.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (isLoadingDetails) {
        Dialog(onDismissRequest = { selectedFoodId = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFF2F4F4F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    } else if (showErrorDialog && detailedFood == null && selectedFoodId != null) {
        Dialog(onDismissRequest = { selectedFoodId = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unable to load food details. Please try again.",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedFoodId = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAAAAAA)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Close", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    } else if (detailedFood != null) {
        FoodDetailsDialog(
            food = detailedFood!!,
            onDismiss = { selectedFoodId = null },
            onConfirm = { weightedFood ->
                scope.launch {
                    val idToken = FirebaseAuthHelper.getIdToken() ?: ""
                    if (idToken.isNotEmpty()) {
                        val quantityGrams =
                            weightedFood.servingDescription.replace("[^0-9]".toRegex(), "")
                                .toIntOrNull() ?: 0
                        val productData = ProductData(
                            product_name = weightedFood.name,
                            quantity_grams = quantityGrams,
                            calories = weightedFood.calories,
                            protein = weightedFood.protein,
                            carbs = weightedFood.carbs,
                            fats = weightedFood.fat
                        )
                        val request = AddMealRequest(
                            idToken = idToken,
                            meal_type = mealType.lowercase(),
                            products = listOf(productData)
                        )
                        val success = ApiService.addMeal(request)
                        if (success) {
                            Log.d("FoodSearchScreen", "Meal saved to DB successfully")
                            onFoodAdded(weightedFood) // Повідомлення про додавання
                        } else {
                            Log.e("FoodSearchScreen", "Failed to save meal to DB")
                        }
                    } else {
                        Log.e("FoodSearchScreen", "IdToken not found")
                    }
                    selectedFoodId = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsDialog(
    food: FoodItem,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    var calculatedFood by remember { mutableStateOf(food) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(weightInput) {
        val weight = weightInput.toFloatOrNull() ?: 0f
        calculatedFood = if (weight > 0) {
            food.calculateNutrientsForWeight(weight)
        } else {
            food
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = food.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (food.description.isNotBlank()) {
                    Text(
                        text = food.description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "Nutrients (${calculatedFood.servingDescription})",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutrientInfo(
                        name = "Calories",
                        value = calculatedFood.calories.toFloat(),
                        unit = "kcal",
                        color = Color.White
                    )
                    NutrientInfo(
                        name = "Protein",
                        value = calculatedFood.protein,
                        unit = "g",
                        color = Color.White
                    )
                    NutrientInfo(
                        name = "Fat",
                        value = calculatedFood.fat,
                        unit = "g",
                        color = Color.White
                    )
                    NutrientInfo(
                        name = "Carbs",
                        value = calculatedFood.carbs,
                        unit = "g",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                            weightInput = newValue
                        }
                    },
                    label = { Text("Weight (g/ml)", color = Color.White) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2F4F4F),
                        unfocusedContainerColor = Color(0xFF2F4F4F),
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAAAAAA)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            val quantityGrams =
                                weightInput.toIntOrNull() ?: 0 // Беремо вагу з введення
                            if (quantityGrams > 0) {
                                val adjustedFood =
                                    calculatedFood.copy(servingDescription = "$quantityGrams g")
                                onConfirm(adjustedFood)
                            }
                        },
                        enabled = weightInput.toIntOrNull() ?: 0 > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64A79B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Add", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = foodItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (foodItem.description.isNotBlank()) {
                Text(
                    text = foodItem.description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "Per 100g",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientInfo(
                    name = "Calories",
                    value = foodItem.calories.toFloat(),
                    unit = "kcal",
                    color = Color.White
                )
                NutrientInfo(
                    name = "Protein",
                    value = foodItem.protein,
                    unit = "g",
                    color = Color.White
                )
                NutrientInfo(
                    name = "Fat",
                    value = foodItem.fat,
                    unit = "g",
                    color = Color.White
                )
                NutrientInfo(
                    name = "Carbs",
                    value = foodItem.carbs,
                    unit = "g",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun NutrientInfo(
    name: String,
    value: Float,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (value == value.toInt().toFloat())
                    value.toInt().toString()
                else String.format("%.1f", value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}