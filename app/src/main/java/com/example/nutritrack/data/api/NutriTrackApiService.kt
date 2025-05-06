package com.example.nutritrack.data.api

import android.util.Log
import com.example.nutritrack.data.user.UpdateUserCurrentWeightRequest
import com.example.nutritrack.data.user.UpdateUserNicknameRequest
import com.example.nutritrack.data.user.UpdateUserProfileDescriptionRequest
import com.example.nutritrack.data.user.UpdateUserProfilePictureRequest
import com.example.nutritrack.model.AddStreakRequest
import com.example.nutritrack.model.ConsultantRegistrationData
import com.example.nutritrack.model.GoalIdResponse
import com.example.nutritrack.model.GoalResponse
import com.example.nutritrack.model.UpdateStreakRequest
import com.example.nutritrack.model.UserData
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class LoginRequest(val idToken: String)

interface ApiServiceInterface {

    @POST("api/Auth/login/consultant")
    suspend fun loginConsultant(@Body request: LoginRequest): Response<Void>

    @POST("api/Auth/register/consultant")
    suspend fun registerConsultant(@Body data: ConsultantRegistrationData): Response<Void>

    @POST("api/Auth/login/user")
    suspend fun loginUser(@Body request: LoginRequest): Response<Void>

    @POST("api/Auth/register/user")
    suspend fun registerUser(@Body data: UserRegistrationData): Response<Void>

    @POST("api/Goal/create-user-goal")
    suspend fun createUserGoal(@Body data: UserGoalData): Response<Void>

    @GET("api/User/get-user-by-uid")
    suspend fun getUserByUid(@Query("uid") uid: String): Response<UserData>

    @GET("api/Goal/get-all-user-goal-ids")
    suspend fun getAllUserGoalIds(@Query("idToken") idToken: String): Response<List<GoalIdResponse>>

    @GET("api/Goal/get-specific-goal-by-id/{goalId}")
    suspend fun getSpecificGoalById(@Path("goalId") goalId: Int): Response<GoalResponse>

    @PUT("api/User/update-profile-picture")
    suspend fun updateProfilePicture(@Body request: UpdateUserProfilePictureRequest): Response<Void>

    @PUT("api/User/update-nickname")
    suspend fun updateNickname(@Body request: UpdateUserNicknameRequest): Response<Void>

    @PUT("api/User/update-profile-description")
    suspend fun updateProfileDescription(@Body request: UpdateUserProfileDescriptionRequest): Response<Void>

    @PUT("api/User/update-current-weight")
    suspend fun updateCurrentWeight(@Body request: UpdateUserCurrentWeightRequest): Response<Void>

    @DELETE("api/Goal/delete-goal/{goalId}")
    suspend fun deleteGoal(
        @Path("goalId") goalId: Int,
        @Query("idToken") idToken: String
    ): Response<Void>

    @POST("api/Streak/add-streak")
    suspend fun addStreak(@Body request: AddStreakRequest): Response<Void>

    @PUT("api/Streak/update-streak")
    suspend fun updateStreak(@Body request: UpdateStreakRequest): Response<Void>
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
            .baseUrl("http://192.168.0.183:5182")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ApiServiceInterface by lazy {
        retrofit.create(ApiServiceInterface::class.java)
    }

    suspend fun checkConsultantExists(idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginConsultant(LoginRequest(idToken))
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to check consultant: $e")
                false
            }
        }
    }

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

    suspend fun checkUserExists(idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginUser(LoginRequest(idToken))
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to check user: $e")
                false
            }
        }
    }

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

    suspend fun getUserByUid(uid: String): UserData? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserByUid(uid)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("ApiService", "Failed to get user by UID: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to get user by UID: $e")
                null
            }
        }
    }

    suspend fun getAllUserGoalIds(idToken: String): List<GoalIdResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllUserGoalIds(idToken)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    Log.e("ApiService", "Failed to get user goal IDs: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to get user goal IDs: $e")
                emptyList()
            }
        }
    }

    suspend fun getSpecificGoalById(goalId: Int): GoalResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSpecificGoalById(goalId)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("ApiService", "Failed to get specific goal by ID: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to get specific goal by ID: $e")
                null
            }
        }
    }

    suspend fun updateProfilePicture(idToken: String, profilePictureUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateUserProfilePictureRequest(idToken, profilePictureUrl)
                val response = apiService.updateProfilePicture(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to update profile picture: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to update profile picture: $e")
                false
            }
        }
    }

    suspend fun updateNickname(idToken: String, newNickname: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateUserNicknameRequest(idToken, newNickname)
                val response = apiService.updateNickname(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to update nickname: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to update nickname: $e")
                false
            }
        }
    }

    suspend fun updateProfileDescription(idToken: String, newDescription: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateUserProfileDescriptionRequest(idToken, newDescription)
                val response = apiService.updateProfileDescription(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to update profile description: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to update profile description: $e")
                false
            }
        }
    }

    suspend fun updateCurrentWeight(idToken: String, newWeight: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateUserCurrentWeightRequest(idToken, newWeight)
                val response = apiService.updateCurrentWeight(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to update current weight: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to update current weight: $e")
                false
            }
        }
    }

    suspend fun deleteGoal(goalId: Int, idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteGoal(goalId, idToken)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to delete goal: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to delete goal: $e")
                false
            }
        }
    }

    suspend fun addStreak(idToken: String, currentStreak: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = AddStreakRequest(idToken, currentStreak)
                val response = apiService.addStreak(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to add streak: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to add streak: $e")
                false
            }
        }
    }

    suspend fun updateStreak(idToken: String, currentStreak: Int, isActive: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateStreakRequest(idToken, currentStreak, isActive)
                val response = apiService.updateStreak(request)
                if (response.isSuccessful) {
                    true
                } else {
                    Log.e("ApiService", "Failed to update streak: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("ApiService", "Failed to update streak: $e")
                false
            }
        }
    }
}