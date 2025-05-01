package com.example.nutritrack.screens.registration.user.create_goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R
import com.example.nutritrack.data.user.UserGoalViewModel

@Composable
fun UserDesiredWeightScreen(
    viewModel: UserGoalViewModel,
    onNextClick: () -> Unit
) {
    // Список ваги від 30 до 200 кг
    val weights = (30..200).toList()
    val selectedDesiredWeight = remember { mutableStateOf(83) } // Початкова бажана вага 83 кг

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // Іконка
            Image(
                painter = painterResource(id = R.drawable.progress_bar_step4),
                contentDescription = "Check icon",
                modifier = Modifier.size(230.dp)
            )

            // Заголовок
            Text(
                text = "Вкажіть вашу бажану вагу",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            // Підзаголовок
            Text(
                text = "Ваша бажана вага це головне для створення плану для вас",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 49.dp),
                textAlign = TextAlign.Center
            )

            // Прямокутник із бажаною вагою і обробкою прокрутки
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color(0xFF2F4F4F), shape = RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            // dragAmount > 0 — прокрутка вниз, dragAmount < 0 — прокрутка вгору
                            val currentIndex = weights.indexOf(selectedDesiredWeight.value)
                            val newIndex = if (dragAmount > 0) {
                                // Прокрутка вниз — зменшуємо вагу
                                (currentIndex - 1).coerceIn(0, weights.size - 1)
                            } else {
                                // Прокрутка вгору — збільшуємо вагу
                                (currentIndex + 1).coerceIn(0, weights.size - 1)
                            }
                            selectedDesiredWeight.value = weights[newIndex]
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${selectedDesiredWeight.value} кг",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Кнопка "Створити план для мене"
        Button(
            onClick = {
                viewModel.setTargetWeight(selectedDesiredWeight.value)
                onNextClick()
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
                text = "Продовжити",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}