package com.application.instagramcloneapp.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.instagramcloneapp.data.datastore.UserPreferences
import com.application.instagramcloneapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(private val authRepository: AuthRepository,
                                         userPreferences: UserPreferences): ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var success by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun authLogin(email: String, password: String) {

        viewModelScope.launch {
            isLoading = true
            error = null
            success = false
            try {
                authRepository.authLogin(email, password)
                success = true
            } catch (e: Exception) {
                error = e.message?: "Login Failed"
            } finally {
                isLoading = false
            }
        }
    }

    val isLoggedIn = userPreferences.isLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    
    fun logout(){
         viewModelScope.launch {
            authRepository.logout()
        }
    }

}