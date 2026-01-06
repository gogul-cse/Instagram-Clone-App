package com.application.instagramcloneapp.screen.singup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import com.application.instagramcloneapp.model.SignupState
import com.application.instagramcloneapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var isLoading = mutableStateOf(false)

    var success = mutableStateOf(false)
        private set

    var error = mutableStateOf<String?>(null)

    var isUsernameAvailable by mutableStateOf<Boolean?>(null)
        private set

    var isUserEmailAvailable by mutableStateOf<Boolean?>(null)
        private set

    private val _signupState = mutableStateOf(SignupState())
    val signupState: State<SignupState> = _signupState


    fun setUsername(instaId: String) {
        _signupState.value = _signupState.value.copy(instaId = instaId)
    }

    fun setBasicDetails(
        userName: String,
        phoneNumber: String,
        email: String,
        password: String
    ) {
        _signupState.value = _signupState.value.copy(
            userName = userName,
            phoneNumber = phoneNumber,
            email = email,
            password = password
        )
    }

    fun setProfileDetails(bio: String, profileImageUrl: String? = null) {
        _signupState.value = _signupState.value.copy(
            bio = bio,
            profileImage = profileImageUrl
        )
    }

    fun createAccount() {
        val state = signupState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            error.value = "Email or password missing"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val uid = repository.authCreateUser(state.email,
                    state.password)
                repository.signup(uid,signupState.value)
                success.value = true
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }



    fun checkUserInstaIdExist(instaId: String){
        if (instaId.isBlank()){
            isUsernameAvailable = null
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                val exists = repository.checkUserInstaIdExist(instaId)
                isUsernameAvailable = !exists
            }catch (e: Exception){
                error.value = e.message
            }finally {
                isLoading.value = false
            }
        }
    }

    fun onUsernameChanged(input: String){
            val newValue = input.lowercase()
            val error = when {
                !newValue.matches(Regex("^[a-z0-9._]*$")) ->  "Only lowercase letters, numbers, . and _ allowed"
                newValue.length in 1..3 ->  "Username must be at least 4 characters"
                else ->  null
            }
         _signupState.value = _signupState.value.copy(
            instaId = newValue,
        )
        isUsernameAvailable = null
    }

    fun checkUserEmail(email: String){
        viewModelScope.launch {
            isLoading.value = true
            try {
                val exist = repository.checkUserEmail(email)
                isUserEmailAvailable = !exist

            }catch (e: Exception){
                error.value = e.message
            }finally {
                isLoading.value = false
            }
        }
    }

    fun updatePostNumber(){
        viewModelScope.launch {
            isLoading.value = true
            try {
                repository.updatePostNumber()

            }catch (e: Exception){
                error.value = e.message
            }finally {
                isLoading.value = false
            }
        }
    }

    fun clearUsernameAvailability() {
        isUsernameAvailable = null
    }

    fun clearEmailState(){
        isUserEmailAvailable = null
    }

    fun updateName(value: String) {
        _signupState.value = _signupState.value.copy(userName =  value)
    }

    fun updatePhone(value: String) {
        _signupState.value = _signupState.value.copy(phoneNumber = value)
    }

    fun updateEmail(value: String) {
        _signupState.value = _signupState.value.copy(email = value)
    }

    fun updatePassword(value: String) {
        _signupState.value = _signupState.value.copy(password = value)
    }

    fun resetSuccess(){
        success.value = false
    }
}
