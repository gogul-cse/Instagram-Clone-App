package com.application.instagramcloneapp.model

import com.google.firebase.Timestamp


data class Post(
    val postId: String="",
    val imageUrl:String="",
    val caption: String = "",
    val userId:String="",
    val userInstId:String="",
    val userProfileImage: String = "",
    val timeStamp: Timestamp? = null,
    val likes: Int = 0
)