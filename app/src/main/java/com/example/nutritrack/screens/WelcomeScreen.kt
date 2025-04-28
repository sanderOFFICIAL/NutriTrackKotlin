package com.example.nutritrack.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R

@Composable
fun WelcomeScreen(
    onNewUserClick: () -> Unit,
    onExistingUserClick: () -> Unit,
    onConsultantClick: () -> Unit,
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Центруємо весь вміст по вертикалі
    ) {
        // Ілюстрація
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Food plate illustration",
            modifier = Modifier.size(200.dp)
        )

        // Текст із відступом 25.dp від ілюстрації
        Text(
            text = "NutriTrack",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE6E6E6),
            modifier = Modifier.padding(top = 25.dp) // Відступ 25.dp від ілюстрації
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }
        // Кнопки прямо під текстом
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp), // Мінімальний відступ від тексту
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp) // Відступ між кнопками 8.dp
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
                onClick = onExistingUserClick,
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
}