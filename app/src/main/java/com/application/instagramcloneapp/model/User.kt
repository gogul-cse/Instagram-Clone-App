package com.application.instagramcloneapp.model

data class User(
    val id: String = "",
    val instaId: String = "",
    val userName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val profileImage:String? = null,
    val followers:Int = 0,
    val following: Int = 0,
    val bio: String? = null,
    val posts:Int = 0
)

data class SignupState(
    val instaId: String = "",
    val userName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val profileImage: String? = null,
    val bio: String = ""
)