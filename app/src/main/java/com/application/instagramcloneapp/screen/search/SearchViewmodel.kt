package com.application.instagramcloneapp.screen.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewmodel  @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var user by mutableStateOf<User?>(null)
        private set

    var isFollowing by mutableStateOf(false)
        private set

    var searchResults by mutableStateOf<List<User>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set


    fun onSearchTextChange(text: String) {
        searchText = text

        viewModelScope.launch {
            isLoading = true
            searchResults = repository.searchUsers(text)
            isLoading = false
        }
    }

    fun loadUserById(userId: String){
        viewModelScope.launch {
            isLoading = true
            try {
                user = repository.loadUserById(userId)
                isLoading = false
            }catch (e: Exception){
                error = e.message
            }
        }
    }

    fun followUser(otherUserId:String,otherUserInstaId: String,profile: String){
        viewModelScope.launch {
            user = user?.copy(followers = (user?.followers ?: 0)+1)
            isFollowing = true
            try {
                repository.addUserFollowingOtherUserFollowers(otherUserId,otherUserInstaId,profile)
                repository.increaseFollowersAndFollowing(otherUserId)
            }catch (e: Exception){
                error = e.message
                user = user?.copy(followers = (user?.followers ?: 1)-1)
                isFollowing = false
            }

        }
    }

    fun unfollowUser(otherUserId: String){
        viewModelScope.launch {
            user = user?.copy(
                followers = (user?.followers ?: 1) - 1
            )
            isFollowing = false

            try {
                repository.removeUserFollowingOtherUserFollowers(otherUserId)
                repository.decreaseFollowersAndFollowing(otherUserId)
            } catch (e: Exception) {
                error = e.message
                user = user?.copy(
                    followers = (user?.followers ?: 0) + 1
                )
                isFollowing = true
            }
        }

    }


    fun checkIsUserFollowing(otherUserId: String){
         viewModelScope.launch {
            isLoading = true
            try {
                isFollowing = repository.checkIsUserFollowing(otherUserId)
                isLoading = false
            }catch (e: Exception){
                error = e.message
            }
        }
    }
}