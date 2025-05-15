package com.example.nutritrack.screens.consultant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.Consultant
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantProfileScreen(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxClients by remember { mutableStateOf(0) }
    var consultantData by remember { mutableStateOf<Consultant?>(null) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            isUploading = true
            uploadError = null

            val storageRef = FirebaseStorage.getInstance().reference
            val fileName =
                "profile_pictures/${FirebaseAuthHelper.getUid()}/${UUID.randomUUID()}.jpg"
            val photoRef = storageRef.child(fileName)

            photoRef.putFile(uri)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        scope.launch {
                            val idToken = FirebaseAuthHelper.getIdToken()
                            if (idToken != null) {
                                val success = ApiService.updateConsultantProfilePicture(
                                    idToken,
                                    downloadUri.toString()
                                )
                                if (success) {
                                    val idToken = FirebaseAuthHelper.getIdToken()
                                    if (idToken != null) {
                                        val consultants = ApiService.getAllConsultants(idToken)
                                        consultantData =
                                            consultants.find { it.consultant_uid == FirebaseAuthHelper.getUid() }
                                        profileImageUri = null
                                    }
                                } else {
                                    uploadError = "Failed to update profile picture"
                                }
                            } else {
                                uploadError = "Failed to get idToken"
                            }
                            isUploading = false
                        }
                    }.addOnFailureListener { e ->
                        uploadError = "Failed to get download URL: ${e.message}"
                        isUploading = false
                    }
                }
                .addOnFailureListener { e ->
                    uploadError = "Failed to upload image: ${e.message}"
                    isUploading = false
                }
        }
    }

    LaunchedEffect(Unit) {
        val idToken = FirebaseAuthHelper.getIdToken()
        if (idToken != null) {
            val consultants = ApiService.getAllConsultants(idToken)
            consultantData = consultants.find { it.consultant_uid == FirebaseAuthHelper.getUid() }
            if (consultantData != null) {
                nickname = consultantData!!.nickname
                description = consultantData!!.profile_description
                maxClients = consultantData!!.max_clients
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2F4F4F),
                modifier = Modifier.height(102.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onHomeClick() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
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
                    onClick = { onSearchClick() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = "Search",
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
                    selected = true,
                    onClick = { onProfileClick() },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF64A79B))
                .padding(top = 75.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Фото профілю
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .clickable {
                            pickImageLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                        )
                    } else if (consultantData?.profile_picture?.isNotEmpty() == true) {
                        AsyncImage(
                            model = consultantData!!.profile_picture,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(110.dp),
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Click to change photo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (isUploading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Повідомлення про помилки або успіх
                if (uploadError != null) {
                    Text(
                        text = uploadError ?: "Unknown error",
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                if (successMessage != null) {
                    Text(
                        text = successMessage ?: "",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Поле для нікнейму
                Text(
                    text = "Nickname:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2F4F4F)
                    )
                ) {
                    TextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Enter your nickname",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White
                        )
                    )
                }

                // Поле для біо
                Text(
                    text = "Bio:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2F4F4F)
                    )
                ) {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Enter your bio",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        },
                        singleLine = false,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White
                        )
                    )
                }

                // Блок для зміни максимальної кількості клієнтів
                Text(
                    text = "Max Clients:",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2F4F4F)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { if (maxClients > 0) maxClients -= 1 },
                            modifier = Modifier
                                .size(80.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_minus),
                                contentDescription = "Decrease max clients",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "$maxClients",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(
                            onClick = { maxClients += 1 },
                            modifier = Modifier
                                .size(80.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Increase max clients",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка для збереження змін
                Button(
                    onClick = {
                        scope.launch {
                            val idToken = FirebaseAuthHelper.getIdToken()
                            if (idToken != null) {
                                var success = true

                                if (nickname != consultantData?.nickname) {
                                    val nicknameSuccess =
                                        ApiService.updateConsultantNickname(idToken, nickname)
                                    if (!nicknameSuccess) {
                                        success = false
                                        uploadError = "Failed to update nickname"
                                    }
                                }

                                if (description != consultantData?.profile_description) {
                                    val descriptionSuccess =
                                        ApiService.updateConsultantProfileDescription(
                                            idToken,
                                            description
                                        )
                                    if (!descriptionSuccess) {
                                        success = false
                                        uploadError = "Failed to update profile description"
                                    }
                                }

                                if (maxClients != consultantData?.max_clients) {
                                    val maxClientsSuccess =
                                        ApiService.updateConsultantMaxClients(idToken, maxClients)
                                    if (!maxClientsSuccess) {
                                        success = false
                                        uploadError = "Failed to update max clients"
                                    }
                                }

                                if (success) {
                                    val idToken = FirebaseAuthHelper.getIdToken()
                                    if (idToken != null) {
                                        val consultants = ApiService.getAllConsultants(idToken)
                                        consultantData =
                                            consultants.find { it.consultant_uid == FirebaseAuthHelper.getUid() }
                                        successMessage = "Profile updated successfully"
                                    }
                                }
                            } else {
                                uploadError = "Failed to get idToken"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    )
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}