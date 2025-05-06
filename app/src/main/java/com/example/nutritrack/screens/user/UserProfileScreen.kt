package com.example.nutritrack.screens.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.UserData
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var userData by remember { mutableStateOf<UserData?>(null) }

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
                                val success =
                                    ApiService.updateProfilePicture(idToken, downloadUri.toString())
                                if (success) {
                                    val uid = FirebaseAuthHelper.getUid()
                                    if (uid != null) {
                                        userData = ApiService.getUserByUid(uid)
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
        val uid = FirebaseAuthHelper.getUid()
        if (uid != null) {
            userData = ApiService.getUserByUid(uid)
            if (userData != null) {
                nickname = userData!!.nickname
                description = userData!!.profile_description
                weight = userData!!.current_weight.toString()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(65.dp)
                .padding(top = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(325.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .clickable(
                        onClick = {
                            pickImageLauncher.launch("image/*")
                        },
                        onClickLabel = "Change profile picture"
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Фото або заглушка
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                } else if (userData?.profile_picture?.isNotEmpty() == true) {
                    AsyncImage(
                        model = userData!!.profile_picture,
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
                        text = "Click to change the photo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

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

            Text(
                text = "Weight:",
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
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    placeholder = {
                        Text(
                            text = "Enter your weight (kg)",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    scope.launch {
                        val idToken = FirebaseAuthHelper.getIdToken()
                        if (idToken != null) {
                            var success = true

                            if (nickname != userData?.nickname) {
                                val nicknameSuccess = ApiService.updateNickname(idToken, nickname)
                                if (!nicknameSuccess) {
                                    success = false
                                    uploadError = "Failed to update nickname"
                                }
                            }

                            if (description != userData?.profile_description) {
                                val descriptionSuccess =
                                    ApiService.updateProfileDescription(idToken, description)
                                if (!descriptionSuccess) {
                                    success = false
                                    uploadError = "Failed to update profile description"
                                }
                            }

                            val newWeight = weight.toIntOrNull() ?: userData?.current_weight ?: 0
                            if (newWeight != userData?.current_weight) {
                                val weightSuccess =
                                    ApiService.updateCurrentWeight(idToken, newWeight)
                                if (!weightSuccess) {
                                    success = false
                                    uploadError = "Failed to update weight"
                                }
                            }

                            if (success) {
                                val uid = FirebaseAuthHelper.getUid()
                                if (uid != null) {
                                    userData = ApiService.getUserByUid(uid)
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