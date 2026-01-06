package com.application.instagramcloneapp.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.LastChat
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MessageInboxViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val repository: MessageRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _chats = MutableStateFlow<List<LastChat>>(emptyList())
    val chats: StateFlow<List<LastChat>> = _chats.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        listenInbox()
    }

    fun openChat(otherUserId: String, navController: NavController) {
        viewModelScope.launch {
            val userSnap = firestore.collection("users")
                .document(otherUserId)
                .get()
                .await()

            val user = userSnap.toObject(User::class.java) ?: return@launch

            val chatId = repository.createChatIfNotExists(
                otherUserId = user.id,
                otherUserInstaId = user.instaId,
                otherUserProfile = user.profileImage
            )

            navController.navigate(
                "${InstagramScreen.ChatScreen.name}/$chatId"
            )
        }
    }


    private fun listenInbox() {
        val uid = auth.currentUser?.uid ?: return

        listener = firestore.collection("chats")
            .whereArrayContains("users", uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                val chats = snapshot.documents

                viewModelScope.launch(Dispatchers.IO) {
                    val inbox = chats.mapNotNull { doc ->
                        val users = doc.get("users") as? List<String> ?: return@mapNotNull null
                        val otherUserId = users.firstOrNull { it != uid } ?: return@mapNotNull null

                        val userSnap = firestore.collection("users")
                            .document(otherUserId)
                            .get()
                            .await()

                        val user = userSnap.toObject(User::class.java) ?: return@mapNotNull null

                        LastChat(
                            chatId = doc.id,
                            instaId = user.instaId,
                            otherUserId = otherUserId,
                            profileImage = user.profileImage,
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTime = doc.getTimestamp("lastMessageTime")
                        )
                    }
                        .sortedByDescending { it.lastMessageTime }
                        _chats.value = inbox

                }
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
