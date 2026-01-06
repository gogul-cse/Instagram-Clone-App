package com.application.instagramcloneapp.model

import com.google.firebase.Timestamp

data class Messages(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String? = null,
    val message: String = "",
    val messageTime: Timestamp? = null
)

data class LastChat(
    val chatId:String = "",
    val users: List<String> = emptyList(),
    val otherUserId: String = "",
    val instaId: String= "",
    val profileImage: String? = null,
    val lastMessage: String ="",
    val lastMessageTime: Timestamp? = null,
)
