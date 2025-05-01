package com.example.nutritrack.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object FirebaseAuthHelper {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Перевірка, чи користувач авторизований
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    // Отримання idToken
    suspend fun getIdToken(): String? {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val tokenResult = user.getIdToken(true).await()
                tokenResult.token
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthHelper", "Failed to get idToken: $e")
            null
        }
    }

    // Отримання UID із idToken
    fun getUid(): String? {
        return auth.currentUser?.uid
    }

    // Вихід із акаунту
    fun signOut() {
        auth.signOut()
    }
}