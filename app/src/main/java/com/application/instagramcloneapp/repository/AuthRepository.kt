package com.application.instagramcloneapp.repository

import com.application.instagramcloneapp.data.datastore.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth,
                                        private val userPreferences: UserPreferences
) {
    suspend fun authLogin(email: String,password: String): String{
        val result = firebaseAuth.signInWithEmailAndPassword(email,password)
            .await()

        val uid =  result.user?.uid ?: throw Exception("Authentication failed")
        userPreferences.saveLogin(uid)
        return uid
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun logout() {
        firebaseAuth.signOut()
        userPreferences.clearLogin()
    }
}