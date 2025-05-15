package com.example.nutritrack.screens.consultant

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.ConsultantRequest
import com.example.nutritrack.model.LinkedRelationship
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConsultantMainScreen(
    onLogoutClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onUserMealClick: (String) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var linkedRelationships by remember { mutableStateOf<List<LinkedRelationship>>(emptyList()) }
    var requests by remember { mutableStateOf<List<ConsultantRequest>>(emptyList()) }
    var showRequestsDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isResponding by remember { mutableStateOf(false) }
    var respondError by remember { mutableStateOf<String?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<String?>(null) }
    var isRemovingClient by remember { mutableStateOf(false) }
    var removeError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val idToken = FirebaseAuthHelper.getIdToken()
        if (idToken != null) {
            try {
                isLoading = true
                errorMessage = null
                // Завантажуємо зв’язки (клієнтів)
                val relationships = ApiService.getLinkedRelationships(idToken)
                linkedRelationships = relationships.filter { it.isActive }
                // Завантажуємо запити
                val requestResponse = ApiService.getAllRequests(idToken)
                requests = requestResponse.filter { it.status == "pending" }
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
            } finally {
                isLoading = false
            }
        } else {
            errorMessage = "Authorization error: IdToken not found"
            isLoading = false
        }
    }

    // Функція для видалення клієнта та його нотаток
    fun removeClient(userUid: String) {
        scope.launch {
            isRemovingClient = true
            removeError = null

            val idToken = FirebaseAuthHelper.getIdToken()
            if (idToken != null) {
                // Отримати goalId користувача
                val goalIdResponse = ApiService.getGoalIdByUserUid(userUid)
                val goalId = goalIdResponse?.goalId

                if (goalId != null) {
                    // Отримати всі нотатки користувача
                    val notes = ApiService.getNotes(goalId, idToken)
                    // Видалити всі нотатки
                    notes.forEach { note ->
                        ApiService.deleteNote(idToken, note.note_id)
                    }
                }

                // Видалити зв’язок із користувачем
                val success = ApiService.removeUser(idToken, userUid)
                if (success) {
                    linkedRelationships = linkedRelationships.filter { it.userUid != userUid }
                } else {
                    removeError = "Failed to remove user"
                }
            } else {
                removeError = "Failed to get idToken"
            }
            isRemovingClient = false
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout Confirmation",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            containerColor = Color(0xFF2F4F4F),
            shape = RoundedCornerShape(12.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Yes", fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("No", fontSize = 16.sp)
                }
            }
        )
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = {
                Text(
                    text = "Remove Client",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this client? This action cannot be undone.",
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            containerColor = Color(0xFF2F4F4F),
            shape = RoundedCornerShape(12.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                        userToRemove?.let { userUid ->
                            removeClient(userUid)
                        }
                        userToRemove = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Yes", fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                        userToRemove = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("No", fontSize = 16.sp)
                }
            }
        )
    }

    if (showRequestsDialog) {
        AlertDialog(
            onDismissRequest = { showRequestsDialog = false },
            title = {
                Text(
                    text = "Pending Requests",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (requests.isEmpty()) {
                        Text(
                            text = "No pending requests",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(requests) { request ->
                                RequestCard(
                                    request = request,
                                    onAccept = {
                                        scope.launch {
                                            isResponding = true
                                            respondError = null
                                            val idToken = FirebaseAuthHelper.getIdToken()
                                            if (idToken != null) {
                                                val success = ApiService.consultantRespondInvite(
                                                    idToken,
                                                    request.userUid,
                                                    true
                                                )
                                                if (success) {
                                                    requests =
                                                        requests.filter { it.requestId != request.requestId }
                                                    // Оновлюємо список клієнтів
                                                    val relationships =
                                                        ApiService.getLinkedRelationships(idToken)
                                                    linkedRelationships =
                                                        relationships.filter { it.isActive }
                                                } else {
                                                    respondError = "Failed to accept request"
                                                }
                                            } else {
                                                respondError = "Failed to get idToken"
                                            }
                                            isResponding = false
                                        }
                                    },
                                    onDecline = {
                                        scope.launch {
                                            isResponding = true
                                            respondError = null
                                            val idToken = FirebaseAuthHelper.getIdToken()
                                            if (idToken != null) {
                                                val success = ApiService.consultantRespondInvite(
                                                    idToken,
                                                    request.userUid,
                                                    false
                                                )
                                                if (success) {
                                                    requests =
                                                        requests.filter { it.requestId != request.requestId }
                                                } else {
                                                    respondError = "Failed to decline request"
                                                }
                                            } else {
                                                respondError = "Failed to get idToken"
                                            }
                                            isResponding = false
                                        }
                                    },
                                    isResponding = isResponding
                                )
                            }
                        }
                    }

                    if (respondError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = respondError!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            containerColor = Color(0xFF2F4F4F),
            shape = RoundedCornerShape(12.dp),
            confirmButton = {
                TextButton(
                    onClick = { showRequestsDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("Close", fontSize = 16.sp)
                }
            },
            dismissButton = {}
        )
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
                        IconButton(onClick = { showLogoutDialog = true }) {
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
                    Box(
                        modifier = Modifier
                            .clickable { showRequestsDialog = true }
                            .background(Color(0xFF64A79B), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_requests),
                                contentDescription = "Requests",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "${requests.size}",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                    onClick = { /* Already on Home screen */ },
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
                    selected = false,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF64A79B))
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
            } else if (linkedRelationships.isEmpty()) {
                Text(
                    text = "No active clients yet.",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 325.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Your Clients",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(linkedRelationships) { relationship ->
                        val user = relationship.user
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2F4F4F))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.3f))
                                        .clickable { onUserMealClick(user.user_uid) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (user.profile_picture.isNotEmpty()) {
                                        AsyncImage(
                                            model = user.profile_picture,
                                            contentDescription = "User Picture",
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_profile),
                                            contentDescription = "No Profile Picture",
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onUserMealClick(user.user_uid) }
                                ) {
                                    Text(
                                        text = user.nickname,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Gender: ${user.gender}",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        userToRemove = user.user_uid
                                        showRemoveDialog = true
                                    },
                                    enabled = !isRemovingClient
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "Remove Client",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (removeError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = removeError!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RequestCard(
    request: ConsultantRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    isResponding: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF64A79B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (request.user.profile_picture.isNotEmpty()) {
                        AsyncImage(
                            model = request.user.profile_picture,
                            contentDescription = "User Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "No Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = request.user.nickname,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gender: ${request.user.gender}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4F4F)),
                    enabled = !isResponding
                ) {
                    if (isResponding) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Accept",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
                Button(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4F4F)),
                    enabled = !isResponding
                ) {
                    if (isResponding) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Decline",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}