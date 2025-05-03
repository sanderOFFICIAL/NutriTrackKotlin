package com.example.nutritrack.screens.registration.user

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.GoogleAuth
import com.example.nutritrack.data.user.UserRegistrationViewModel
import kotlinx.coroutines.launch

@Composable
fun UserNicknameScreen(
    viewModel: UserRegistrationViewModel,
    onRegistrationSuccess: () -> Unit,
    navController: NavHostController,
) {
    val nickname = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        GoogleAuth.initialize(context)
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            coroutineScope.launch {
                try {
                    val idToken = GoogleAuth.handleSignInResult(result.data)
                    viewModel.setIdToken(idToken)

                    val userExists = ApiService.checkUserExists(idToken)
                    val consultantExist = ApiService.checkConsultantExists(idToken)
                    if (consultantExist) {
                        snackbarHostState.showSnackbar(
                            message = "Error: You can't create an account because you are registered as a consultant for this email",
                            duration = SnackbarDuration.Short
                        )
                        navController.navigate("welcome_screen") {
                            popUpTo("user_nickname_screen") { inclusive = true }
                        }
                    } else if (userExists) {
                        snackbarHostState.showSnackbar(
                            message = "Error: The user with this account is already logged in, please log in",
                            duration = SnackbarDuration.Short
                        )
                        navController.navigate("welcome_screen") {
                            popUpTo("user_nickname_screen") { inclusive = true }
                        }
                    } else {
                        viewModel.setNickname(nickname.value)
                        val success = ApiService.registerUser(viewModel.userData.value)
                        if (success) {
                            viewModel.clearData()
                            onRegistrationSuccess()
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Error: Unable to register user",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }

                } catch (e: Exception) {
                    Log.e("UserNicknameScreen", "Google Sign-In failed: $e")
                    snackbarHostState.showSnackbar(
                        message = "Authorization error: $e",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF64A79B))
                .padding(16.dp)
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.progress_bar_step6),
                contentDescription = "Progress bar step 2",
                modifier = Modifier
                    .size(420.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 252.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Please provide your name",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "This is the name your consultants will see",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 40.dp),
                        textAlign = TextAlign.Center
                    )

                    TextField(
                        value = nickname.value,
                        onValueChange = { nickname.value = it },
                        label = { Text("Your name", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2F4F4F),
                            unfocusedContainerColor = Color(0xFF2F4F4F),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 22.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Button(
                    onClick = {
                        if (nickname.value.isNotEmpty()) {
                            GoogleAuth.signIn(launcher)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please enter a nickname",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
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
                        text = "Register",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}