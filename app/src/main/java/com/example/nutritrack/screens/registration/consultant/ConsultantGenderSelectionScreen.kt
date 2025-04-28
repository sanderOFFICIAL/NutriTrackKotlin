package com.example.nutritrack.screens.registration.consultant

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
fun ConsultantGenderSelectionScreen(
    onFemaleSelected: () -> Unit,
    onMaleSelected: () -> Unit,
    onNextClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp)
    ) {
        // Прогрес-бар із фіксованою позицією зверху
        Image(
            painter = painterResource(id = R.drawable.progress_bar_step1),
            contentDescription = "Progress bar step 1",
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.TopCenter)
                .padding(top = 200.dp) // Зменшуємо відступ зверху
        )

        // Основний вміст
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 252.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Вкажіть вашу стать",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            // Підзаголовок
            Text(
                text = "Ваша стать потрібна для відображення в профілі для клієнтів",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp),
                textAlign = TextAlign.Center
            )

            // Кнопки
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Button(
                    onClick = {
                        onFemaleSelected()
                        onNextClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Жінка",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        onMaleSelected()
                        onNextClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F4F4F)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Чоловік",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}