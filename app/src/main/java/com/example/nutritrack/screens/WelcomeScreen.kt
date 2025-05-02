package com.example.nutritrack.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.data.auth.GoogleAuth
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onNewUserClick: () -> Unit,
    onConsultantClick: () -> Unit,
    onLoginAsUserClick: () -> Unit,
    onLoginAsConsultantClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Ініціалізуємо GoogleAuth при першому запуску екрану
    LaunchedEffect(Unit) {
        GoogleAuth.initialize(context)
    }

    // Стани для відображення процесу завантаження, помилок і токена
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var idToken by remember { mutableStateOf<String?>(null) }

    // Стан для управління шторкою
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }

    // Лаунчер для отримання результатів Google авторизації
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        scope.launch {
            isLoading = true
            try {
                idToken = GoogleAuth.handleSignInResult(result.data)
                if (idToken == null) {
                    errorMessage = "Помилка авторизації: Не вдалося отримати токен"
                } else {
                    showBottomSheet.value = true // ✅ відкриваємо тільки після успіху
                }
            } catch (e: Exception) {
                errorMessage = "Помилка авторизації: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    // Показуємо помилку через Toast лише один раз
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            errorMessage = null // Очищаємо після показу
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Food plate illustration",
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = "NutriTrack",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE6E6E6),
            modifier = Modifier.padding(top = 25.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            Button(
                onClick = onNewUserClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E9393)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Новий користувач",
                    fontSize = 21.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = onConsultantClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E9393)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Новий консультант",
                    fontSize = 21.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    // Запускаємо авторизацію через Google і показуємо шторку
                    GoogleAuth.signIn(launcher)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F4F4F)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Вже маю акаунт",
                    fontSize = 21.sp,
                    color = Color.White
                )
            }
        }
    }

    // Шторка для вибору типу акаунту
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
                FirebaseAuth.getInstance().signOut() // Вихід при закритті шторки
            },
            sheetState = sheetState,
            containerColor = Color(0xFF2F4F4F)
        ) {
            var bottomSheetLoading by remember { mutableStateOf(false) }
            var bottomSheetError by remember { mutableStateOf<String?>(null) }

            // Показуємо помилку як Toast лише один раз
            bottomSheetError?.let { message ->
                LaunchedEffect(message) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    bottomSheetError = null // Очищаємо після показу
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(
                    text = "Оберіть тип акаунту",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        scope.launch {
                            bottomSheetLoading = true
                            try {
                                // Чекаємо, поки idToken не буде доступним
                                if (idToken == null) {
                                    bottomSheetError = "Очікуйте завершення авторизації..."
                                    return@launch
                                }

                                val result = FirebaseAuthHelper.loginAsUser()
                                result.fold(
                                    onSuccess = {
                                        sheetState.hide()
                                        showBottomSheet.value = false
                                        onLoginAsUserClick()
                                    },
                                    onFailure = { exception ->
                                        bottomSheetError =
                                            "Користувача не знайдено. Спробуйте увійти як консультант: ${exception.message}"
                                    }
                                )
                            } catch (e: Exception) {
                                bottomSheetError = "Помилка: ${e.message}"
                            } finally {
                                bottomSheetLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E9393)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !bottomSheetLoading
                ) {
                    if (bottomSheetLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Увійти як користувач",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            bottomSheetLoading = true
                            try {
                                // Чекаємо, поки idToken не буде доступним
                                if (idToken == null) {
                                    bottomSheetError = "Очікуйте завершення авторизації..."
                                    return@launch
                                }

                                val result = FirebaseAuthHelper.loginAsConsultant()
                                result.fold(
                                    onSuccess = {
                                        sheetState.hide()
                                        showBottomSheet.value = false
                                        onLoginAsConsultantClick()
                                    },
                                    onFailure = { exception ->
                                        bottomSheetError =
                                            "Консультанта не знайдено. Спробуйте увійти як користувач: ${exception.message}"
                                    }
                                )
                            } catch (e: Exception) {
                                bottomSheetError = "Помилка: ${e.message}"
                            } finally {
                                bottomSheetLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E9393)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !bottomSheetLoading
                ) {
                    if (bottomSheetLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Увійти як консультант",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}