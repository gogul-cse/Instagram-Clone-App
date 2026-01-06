package com.application.instagramcloneapp.screen.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.repository.PostRepository
import com.application.instagramcloneapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import com.application.instagramcloneapp.model.Followers
import com.application.instagramcloneapp.model.Following
import com.application.instagramcloneapp.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()


    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private val _imageUri = mutableStateOf<Uri?>(null)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    val imageUri: State<Uri?> = _imageUri

    private val _followers = MutableStateFlow<List<Followers>>(emptyList())
    val followers = _followers.asStateFlow()

    private val _following = MutableStateFlow<List<Following>>(emptyList())
    val following = _following.asStateFlow()

    var isFollowLoading by mutableStateOf(false)
        private set


    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun clear() {
        _imageUri.value = null
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                _user.value = repository.getCurrentUser()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadPost(imageUrl: String, caption: String){
        viewModelScope.launch {
            try {
                isLoading = true
                val newPost = postRepository.uploadPost(imageUrl,caption)
                _posts.value = listOf(newPost) + _posts.value
            }catch (e: Exception){
                error = e.message
            }finally {
                isLoading = false
            }
        }
    }

    fun getPostByTime(){
        viewModelScope.launch {
            try {
                isLoading = true
                _posts.value = postRepository.getPostByTime()
            }catch (e: Exception){
                error = e.message
            }finally {
                isLoading = false
            }
        }
    }

    fun getOthersPostById(otherUserId: String){
        viewModelScope.launch {
            try {
                isLoading = true
                _posts.value = postRepository.getOthersPostById(otherUserId)
            }catch (e: Exception){
                error = e.message
            }finally {
                isLoading = false
            }
        }
    }

    fun loadFollowers(userId: String) {
        viewModelScope.launch {
            isFollowLoading = true
            try {
                _followers.value = repository.getFollowers(userId)
            } finally {
                isFollowLoading = false
            }
        }
    }

    fun loadFollowing(userId: String) {
        viewModelScope.launch {
            isFollowLoading = true
            try {
                _following.value = repository.getFollowing(userId)
            } finally {
                isFollowLoading = false
            }
        }
    }

    fun removeFollower(otherUserId: String){
        viewModelScope.launch {
            val previousFollowers = _followers.value
            _followers.value = previousFollowers.filterNot {
                it.followersId == otherUserId
            }
            _user.value = _user.value?.copy(followers = (_user.value?.followers ?: 1)-1)
            isLoading = true
            try {
                repository.removeUserFollowerAndOtherUserFollowing(otherUserId)

            }catch (e: Exception){
                _followers.value = previousFollowers
                _user.value = _user.value?.copy(
                    followers = (_user.value?.followers ?: 0) + 1
                )
                error = e.message
            }finally {
                isLoading = false
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
            _posts.value = _posts.value.filterNot { it.postId == postId }
        }
    }

    fun updateUserProfile(profileImage: String?, username: String, bio: String,onSuccess:()-> Unit
    ){
        viewModelScope.launch {
            isLoading = true
            try {
                repository.updateUserProfile(profileImage, username, bio)
                _user.value = _user.value?.copy(
                    profileImage = profileImage ?: _user.value?.profileImage,
                    userName = username,
                    bio = bio
                )

                onSuccess()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

}