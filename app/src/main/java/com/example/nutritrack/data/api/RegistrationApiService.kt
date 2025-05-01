package com.example.nutritrack.data.api

import android.util.Log
import com.example.nutritrack.model.ConsultantRegistrationData
import com.example.nutritrack.model.UserGoalData
import com.example.nutritrack.model.UserRegistrationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class LoginRequest(val idToken: String)

interface ApiServiceInterface {
    // Ендпоінти для консультанта
    @POST("api/Auth/login/consultant")
    suspend fun loginConsultant(@Body request: LoginRequest): Response<Void>

    @POST("api/Auth/register/consultant")
    suspend fun registerConsultant(@Body data: ConsultantRegistrationData): Response<Void>

    // Ендпоінти для користувача
    @POST("api/Auth/login/user")
    suspend fun loginUser(@Body request: LoginRequest): Response<Void>

    @POST("api/Auth/register/user")
    suspend fun registerUser(@Body data: UserRegistrationData): Response<Void>

    @POST("api/Goal/create-user-goal")
    suspend fun createUserGoal(@Body data: UserGoalData): Response<Void>

}

object ApiService {
    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("http://192.168.0.183:5182") // Заміни на URL твого API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ApiServiceInterface by lazy {
        retrofit.create(ApiServiceInterface::class.java)
    }

    // Перевірка, чи консультант існує
    suspend fun checkConsultantExists(idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginConsultant(LoginRequest(idToken))
                response.isSuccessful // Якщо 200 — консультант існує
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to check consultant: $e")
                false // Якщо помилка — вважаємо, що консультанта немає
            }
        }
    }

    // Реєстрація консультанта
    suspend fun registerConsultant(data: ConsultantRegistrationData): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registerConsultant(data)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to register consultant: $e")
                false
            }
        }
    }

    // Перевірка, чи користувач існує
    suspend fun checkUserExists(idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginUser(LoginRequest(idToken))
                response.isSuccessful // Якщо 200 — користувач існує
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to check user: $e")
                false // Якщо помилка — вважаємо, що користувача немає
            }
        }
    }

    // Реєстрація користувача
    suspend fun registerUser(data: UserRegistrationData): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registerUser(data)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to register user: $e")
                false
            }
        }
    }

    suspend fun createUserGoal(data: UserGoalData): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createUserGoal(data)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to create user goal: $e")
                false
            }
        }
    }

}