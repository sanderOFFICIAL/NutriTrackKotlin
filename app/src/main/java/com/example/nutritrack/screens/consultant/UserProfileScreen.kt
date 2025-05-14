package com.example.nutritrack.screens.consultant

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.UserData
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileScreen(
    userUid: String,
    onBackClick: () -> Unit,
    onClientAdded: () -> Unit
) {
    var user by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSendingInvite by remember { mutableStateOf(false) }
    var inviteError by remember { mutableStateOf<String?>(null) }
    var inviteSuccess by remember { mutableStateOf(false) }
    var isRemovingClient by remember { mutableStateOf(false) }
    var removeError by remember { mutableStateOf<String?>(null) }
    var hasLinkedRelationship by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Стан для діалогу
    var dialogAction by remember { mutableStateOf<String?>(null) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userUid) {
        try {
            isLoading = true
            errorMessage = null
            val idToken = FirebaseAuthHelper.getIdToken() ?: run {
                errorMessage = "Authorization error: IdToken not found"
                return@LaunchedEffect
            }

            // Перевіряємо наявність зв’язку між консультантом і користувачем
            val relationships = ApiService.getLinkedRelationships(idToken)
            hasLinkedRelationship = relationships.any { it.isActive && it.userUid == userUid }
            // Перевіряємо, чи було надіслано запрошення (але ще не прийнято)
            inviteSuccess = relationships.any { !it.isActive && it.userUid == userUid }

            // Отримуємо дані користувача
            val userData = ApiService.getUserByUid(userUid)
            user = userData
            if (user == null) {
                errorMessage = "User not found"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Функція для надсилання запрошення користувачу
    fun sendInvite() {
        scope.launch {
            isSendingInvite = true
            inviteError = null
            inviteSuccess = false

            val idToken = FirebaseAuthHelper.getIdToken()
            if (idToken != null) {
                val success = ApiService.sendInviteToUser(idToken, userUid)
                if (success) {
                    inviteSuccess = true
                    onClientAdded()
                } else {
                    inviteError = "You already sent an invitation or an error occurred"
                }
            } else {
                inviteError = "Failed to get idToken"
            }
            isSendingInvite = false
        }
    }

    // Функція для видалення клієнта
    fun removeClient() {
        scope.launch {
            isRemovingClient = true
            removeError = null

            val idToken = FirebaseAuthHelper.getIdToken()
            if (idToken != null) {
                val success = ApiService.removeUser(idToken, userUid)
                if (success) {
                    hasLinkedRelationship = false
                    inviteSuccess = false
                    onBackClick()
                } else {
                    removeError = "Failed to remove client"
                }
            } else {
                removeError = "Failed to get idToken"
            }
            isRemovingClient = false
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = dialogTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = dialogText,
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            containerColor = Color(0xFF2F4F4F),
            shape = RoundedCornerShape(12.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        when (dialogAction) {
                            "send_invite" -> sendInvite()
                            "remove_client" -> removeClient()
                        }
                    },
                    colors = textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Yes", fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("No", fontSize = 16.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(45.dp)
                )
            }
            Text(
                text = "User Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.height(100.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else if (user != null) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (user!!.profile_picture.isNotEmpty()) {
                    AsyncImage(
                        model = user!!.profile_picture,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "No Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(110.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user!!.nickname,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user!!.profile_description.ifEmpty { "No bio available" },
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Gender: ${user!!.gender}",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Height: ${user!!.height} cm",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Weight: ${user!!.current_weight} kg",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (inviteError != null) {
                Text(
                    text = inviteError!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            } else if (inviteSuccess && !hasLinkedRelationship) {
                Text(
                    text = "Invite sent successfully!",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            } else if (hasLinkedRelationship) {
                Text(
                    text = "Client added successfully!",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            if (removeError != null) {
                Text(
                    text = removeError!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (hasLinkedRelationship) {
                Button(
                    onClick = {
                        dialogAction = "remove_client"
                        dialogTitle = "Remove Client"
                        dialogText =
                            "Are you sure you want to remove this client? This action cannot be undone."
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    enabled = !isRemovingClient
                ) {
                    if (isRemovingClient) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Remove Client",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        dialogAction = "send_invite"
                        dialogTitle = "Send Invite"
                        dialogText = "Are you sure you want to send an invite to this user?"
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    enabled = !isSendingInvite && !inviteSuccess && !hasLinkedRelationship
                ) {
                    if (isSendingInvite) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Add as Client",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}