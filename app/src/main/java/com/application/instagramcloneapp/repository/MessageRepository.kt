package com.application.instagramcloneapp.repository

import com.application.instagramcloneapp.model.LastChat
import com.application.instagramcloneapp.model.Messages
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepository  @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
){
    private fun currentUid(): String =
        auth.currentUser?.uid ?: throw Exception("User not logged in")

    fun getChatId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }

    suspend fun createChatIfNotExists(
        otherUserId: String,
        otherUserInstaId: String,
        otherUserProfile: String?
    ): String {
        val myUid = currentUid()
        val chatId = getChatId(myUid, otherUserId)
        val chatRef = firestore.collection("chats").document(chatId)

        val snapshot = chatRef.get().await()
        if (!snapshot.exists()) {
            chatRef.set(
                LastChat(
                    chatId = chatId,
                    users = listOf(myUid,otherUserId),
                    otherUserId = otherUserId,
                    instaId = otherUserInstaId,
                    profileImage = otherUserProfile,
                    lastMessage = "",
                    lastMessageTime = Timestamp.now()
                    )
            ).await()
        }
        return chatId
    }

    fun listenMessages(
        chatId: String,
        onUpdate: (List<Messages>) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("messageTime")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(Messages::class.java)
                        ?.copy(id = it.id)
                } ?: emptyList()
                onUpdate(messages)
            }
    }

    suspend fun sendMessage(chatId: String,reciverId: String, text: String) {
        val senderId = currentUid()

        val messageId = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        val message = Messages(
            id = messageId.id,
            senderId = senderId,
            receiverId = reciverId,
            message = text,
            messageTime = Timestamp.now()
        )
        firestore.runBatch { batch ->
            batch.set(messageId,message)

            batch.update(
                firestore.collection("chats")
                    .document(chatId),
                        mapOf(
                            "lastMessage" to text,
                            "lastMessageTime" to FieldValue.serverTimestamp()
                        )
            )

        }.await()
    }

}