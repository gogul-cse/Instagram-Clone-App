package com.application.instagramcloneapp.screen.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.instagramcloneapp.model.Post
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.repository.PostRepository
import com.application.instagramcloneapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PostRepository,private val userRepository: UserRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var suggestions by mutableStateOf<List<User>>(emptyList())
        private set

    fun loadFeed() {
        viewModelScope.launch {
            isLoading = true
            try {
                posts = repository.getFeedPosts()
                Log.d("FEED_VM", "Posts size = ${posts.size}")
            } catch (e: Exception) {
                error = e.message
                Log.e("FEED_VM", "Error", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                suggestions = userRepository.getSuggestedUsers()
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    fun followUserOptimistic(user: User) {
        suggestions = suggestions.filterNot { it.id == user.id }
        viewModelScope.launch {
            try {
                userRepository.addUserFollowingOtherUserFollowers(
                    othersUserId = user.id,
                    otherUserInstaId = user.instaId,
                    profile = user.profileImage ?: ""
                )
                userRepository.increaseFollowersAndFollowing(user.id)
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

}