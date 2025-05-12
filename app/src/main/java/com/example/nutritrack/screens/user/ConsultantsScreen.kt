package com.example.nutritrack.screens.user

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutritrack.R
import com.example.nutritrack.data.api.ApiService
import com.example.nutritrack.data.auth.FirebaseAuthHelper
import com.example.nutritrack.model.Consultant
import com.example.nutritrack.model.LinkedRelationship
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantsScreen(
    onBackClick: () -> Unit,
    onNotebookClick: () -> Unit,
    onProfileClick: () -> Unit,
    onConsultantProfileClick: (String) -> Unit
) {
    var consultants by remember { mutableStateOf<List<Consultant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var linkedRelationship by remember { mutableStateOf<LinkedRelationship?>(null) }

    val scope = rememberCoroutineScope()

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

                val relationships = ApiService.getLinkedRelationships(idToken)
                linkedRelationship = relationships.firstOrNull { it.isActive }

                if (linkedRelationship == null) {
                    val response = ApiService.getAllConsultants(idToken)
                    consultants = response
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (linkedRelationship != null) {
        ConsultantNotesScreen(
            linkedRelationship = linkedRelationship!!,
            onBackClick = onBackClick,
            onNotebookClick = onNotebookClick,
            onProfileClick = onProfileClick,
            onConsultantProfileClick = onConsultantProfileClick
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF2F4F4F),
                    modifier = Modifier.height(102.dp)
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { onNotebookClick() },
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
                        selected = true,
                        onClick = { /* Already on consultants screen */ },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_communication),
                                contentDescription = "Consultants",
                                modifier = Modifier.size(35.dp),
                                tint = Color.White
                            )
                        },
                        label = {
                            Text(
                                text = "Consultants",
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
                Spacer(modifier = Modifier.height(38.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            "Search by nickname...",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
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
                    keyboardActions = KeyboardActions(onSearch = { /* Handle search if needed */ }),
                    singleLine = true
                )

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
                } else {
                    val filteredConsultants = if (searchQuery.isNotEmpty()) {
                        consultants.filter { it.nickname.contains(searchQuery, ignoreCase = true) }
                    } else {
                        consultants
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredConsultants) { consultant ->
                            ConsultantCard(
                                consultant = consultant,
                                onClick = { onConsultantProfileClick(consultant.consultant_uid) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultantCard(consultant: Consultant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (consultant.profile_picture.isNotEmpty()) {
                    AsyncImage(
                        model = consultant.profile_picture,
                        contentDescription = "Profile Picture",
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
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = consultant.nickname,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Experience: ${consultant.experience_years} years",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "Clients: ${consultant.current_clients}/${consultant.max_clients}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                val availableSlots = consultant.max_clients - consultant.current_clients
                Text(
                    text = "Available: $availableSlots slots",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}