package com.application.instagramcloneapp.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.instagramcloneapp.model.Messages
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel  @Inject constructor(private val firestore: FirebaseFirestore,
    private val repository: MessageRepository,
    private val auth: FirebaseAuth
) : ViewModel()  {


    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _chatUser = MutableStateFlow<User?>(null)
    val chatUser = _chatUser.asStateFlow()

    private var chatId: String? = null
    private var otherUserId: String? = null

    private var listener: ListenerRegistration? = null

    fun initChat(chatId:String) {
        this.chatId = chatId
        val myUid = auth.currentUser?.uid ?: return

        firestore.collection("chats")
            .document(chatId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) return@addOnSuccessListener

                val users = doc.get("users") as? List<String> ?: return@addOnSuccessListener
                otherUserId = users.firstOrNull { it != myUid } ?: return@addOnSuccessListener

                firestore.collection("users")
                    .document(otherUserId!!)
                    .get()
                    .addOnSuccessListener {
                        _chatUser.value = it.toObject(User::class.java)
                    }
            }

        listener?.remove()
        listener = repository.listenMessages(chatId) {
            _messages.value = it
        }
    }

    fun sendMessage(text: String) {
        val id = chatId ?: return
        val receiver = otherUserId ?: return
        viewModelScope.launch {
            repository.sendMessage(id, receiver,text)
        }
    }

    fun isFromMe(message: Messages): Boolean {
        return message.senderId == auth.currentUser?.uid
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }

}