package com.application.instagramcloneapp.model

data class Followers(
    val followersId: String="",
    val followersInstaId: String="",
    val followersProfile: String = ""
)

data class Following(
    val followingId: String="",
    val followingInstaId: String="",
    val followingProfile: String = ""
)

data class Follow(
    val userId: String,
    val userInstaId: String = "",
    val username: String = "",
    val profileImage: String = "",
    val isFollowing: Boolean = true
)
