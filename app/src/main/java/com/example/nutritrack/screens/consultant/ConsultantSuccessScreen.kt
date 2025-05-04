package com.example.nutritrack.screens.consultant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConsultantSuccessScreen(
    onNavigateToMainScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Заголовок
                Text(
                    text = "Thank you for registering!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You have successfully registered as a consultant.",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 40.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "✔",
                    fontSize = 100.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            Button(
                onClick = { onNavigateToMainScreen() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4F4F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Go to the home screen",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}