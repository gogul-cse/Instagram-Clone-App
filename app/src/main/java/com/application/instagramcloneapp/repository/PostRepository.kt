package com.application.instagramcloneapp.repository

import android.util.Log
import com.application.instagramcloneapp.model.Following
import com.application.instagramcloneapp.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(private val firestore: FirebaseFirestore,
                                    private val auth: FirebaseAuth) {

    private fun currentUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")
    }

    suspend fun getUserData(): Pair<String,String>{
        val uid = currentUserId()
        val datas = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        val instaId =  datas.getString("instaId")?: throw IllegalStateException("instaId missing")
        val profileImage = datas.getString("profileImage") ?: ""
        return Pair(instaId,profileImage)
    }
    suspend fun uploadPost(imageUrl: String, caption: String):Post {
        val uid = currentUserId()
        val postId = firestore.collection("posts").document().id

        val (instaId,profileImage) = getUserData()
        val post = Post(
            postId = postId,
            imageUrl = imageUrl,
            caption = caption,
            userId = uid,
            userInstId = instaId,
            userProfileImage = profileImage,
            timeStamp = Timestamp.now()

        )

        firestore.collection("posts")
            .document(postId)
            .set(post)
            .await()

        firestore.collection("users")
            .document(uid)
            .collection("ProfilePost")
            .document(postId)
            .set(post)
            .await()
        return post
    }

    suspend fun getPostByTime(): List<Post>{
        val uid = currentUserId()
        return firestore.collection("users")
            .document(uid)
            .collection("ProfilePost")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Post::class.java)
    }

    suspend fun getOthersPostById(otherUserId: String): List<Post>{
        return firestore.collection("users")
            .document(otherUserId)
            .collection("ProfilePost")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Post::class.java)
    }

    suspend fun getFollowingUserIds(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("following")
            .get()
            .await()

        return snapshot.toObjects(Following::class.java)
            .map { it.followingId }

    }

    suspend fun getFeedPosts(): List<Post> {
        val uid = currentUserId()
        val followingIds = getFollowingUserIds().toMutableList()
        followingIds.add(uid)

        Log.d("FEED", "Following IDs = $followingIds")


        if (followingIds.isEmpty()) return emptyList()

        return firestore.collection("posts")
            .whereIn("userId", followingIds)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Post::class.java)
    }

    suspend fun deletePost(postId: String){
        firestore.collection("posts")
            .document(postId)
            .delete()
            .await()

        firestore.collection("users")
            .document(currentUserId())
            .collection("ProfilePost")
            .document(postId)
            .delete()
            .await()

    }


}