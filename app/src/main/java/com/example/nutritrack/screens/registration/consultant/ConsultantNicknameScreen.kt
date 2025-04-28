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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritrack.R

@Composable
fun ConsultantNicknameScreen(
    onNicknameSelected: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    val nickname = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF64A79B))
            .padding(16.dp)
    ) {
        // Прогрес-бар із фіксованою позицією зверху
        Image(
            painter = painterResource(id = R.drawable.progress_bar_step2),
            contentDescription = "Progress bar step 2",
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.TopCenter)
                .padding(top = 200.dp) // Зменшуємо відступ зверху
        )

        // Основний вміст (заголовок, підзаголовок, текстове поле, кнопка)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 252.dp), // 230.dp (висота прогрес-бару) + 8.dp (відступ зверху) + 14.dp (для відстані 30.dp до тексту заголовка)
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
                    text = "Вкажіть ваше ім'я",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                // Підзаголовок
                Text(
                    text = "Це ім'я, яке бачитимуть ваші клієнти",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 40.dp),
                    textAlign = TextAlign.Center
                )

                // Текстове поле для введення псевдоніма
                TextField(
                    value = nickname.value,
                    onValueChange = { nickname.value = it },
                    label = { Text("Ваш псевдонім", fontSize = 14.sp) },
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

            // Кнопка "Продовжити"
            Button(
                onClick = {
                    onNicknameSelected(nickname.value)
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
}