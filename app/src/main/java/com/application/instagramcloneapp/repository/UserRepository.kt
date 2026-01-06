package com.application.instagramcloneapp.repository

import com.application.instagramcloneapp.data.datastore.UserPreferences
import com.application.instagramcloneapp.model.Followers
import com.application.instagramcloneapp.model.Following
import com.application.instagramcloneapp.model.SignupState
import com.application.instagramcloneapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firestore: FirebaseFirestore,private val auth: FirebaseAuth,
                                        private val userPreferences: UserPreferences) {

    private fun currentUid() = auth.currentUser?.uid ?: throw Exception("User not authenticated")

    suspend fun authCreateUser(email: String,password: String): String{
        val authResult = auth.createUserWithEmailAndPassword(email, password)
            .await()
        return authResult.user?.uid ?: throw Exception("Authentication failed")
    }

    suspend fun signup(uid:String,state: SignupState): User{

         val user = User(id=uid, instaId = state.instaId, userName = state.userName, phoneNumber = state.phoneNumber,
             email = state.email, password = state.password,
             profileImage = state.profileImage, bio = state.bio)
         firestore.collection("users")
             .document(uid)
             .set(user)
             .await()
         userPreferences.saveLogin(uid)
         return user
    }


    suspend fun checkUserInstaIdExist(instaId: String): Boolean{
        val snapshot = firestore.collection("users")
            .whereEqualTo("instaId",instaId)
            .limit(1)
            .get()
            .await()
        return !snapshot.isEmpty

    }

    suspend fun checkUserEmail(email:String): Boolean{
        val existMail = firestore.collection("users")
            .whereEqualTo("email",email)
            .limit(1)
            .get()
            .await()
        return !existMail.isEmpty
    }

    suspend fun getCurrentUser(): User {

        val snapshot = firestore.collection("users")
            .document(currentUid())
            .get()
            .await()

        return snapshot.toObject(User::class.java)
            ?: throw Exception("User data not found")
    }

    suspend fun updatePostNumber(){
        firestore.collection("users")
            .document(currentUid())
            .update("posts", FieldValue.increment(1))
            .await()
    }

    suspend fun searchUsers(username: String): List<User> {
        if (username.isBlank()) return emptyList()

        return firestore.collection("users")
            .orderBy("instaId")
            .startAt(username.lowercase())
            .endAt(username.lowercase() + "\uf8ff")
            .get()
            .await()
            .toObjects(User::class.java)
    }

    suspend fun loadUserById(userId: String): User{
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
            ?: throw Exception("User data not found")
    }

    //Add Followers and Following
    suspend fun increaseFollowersAndFollowing(otherUserId: String){
        firestore.collection("users")
            .document(currentUid())
            .update("following", FieldValue.increment(1))
            .await()

        firestore.collection("users")
            .document(otherUserId)
            .update("followers", FieldValue.increment(1))
            .await()
    }

    suspend fun addUserFollowingOtherUserFollowers(othersUserId:String, otherUserInstaId: String,profile: String){
        val snapshot = firestore.collection("users")
            .document(currentUid())
            .get()
            .await()
        val instaId = snapshot.getString("instaId") ?: throw Exception("User Not found")
        val userProfile = snapshot.getString("profileImage") ?: ""

        val following = Following(othersUserId,otherUserInstaId,profile)
        val followers = Followers(currentUid(),instaId,userProfile)

        firestore.collection("users")
            .document(currentUid())
            .collection("following")
            .document(othersUserId)
            .set(following)
            .await()

        firestore.collection("users")
            .document(othersUserId)
            .collection("followers")
            .document(currentUid())
            .set(followers)
            .await()


    }

    //Remove Followers and Following
    suspend fun removeUserFollowingOtherUserFollowers(othersUserId: String){
        firestore.collection("users")
            .document(currentUid())
            .collection("following")
            .document(othersUserId)
            .delete()
            .await()

        firestore.collection("users")
            .document(othersUserId)
            .collection("followers")
            .document(currentUid())
            .delete()
            .await()

    }

    suspend fun decreaseFollowersAndFollowing(otherUserId: String){
        firestore.collection("users")
            .document(currentUid())
            .update("following", FieldValue.increment(-1))
            .await()

        firestore.collection("users")
            .document(otherUserId)
            .update("followers", FieldValue.increment(-1))
            .await()
    }


    suspend fun removeUserFollowerAndOtherUserFollowing(otherUserId: String){
        val user = firestore.collection("users")
            .document(currentUid())
        val otherUser = firestore.collection("users")
            .document(otherUserId)

        user.update("followers", FieldValue.increment(-1)).await()

        user.collection("followers")
            .document(otherUserId)
            .delete()
            .await()


        otherUser.update("following", FieldValue.increment(-1)).await()

        otherUser.collection("following")
            .document(currentUid())
            .delete()
            .await()

    }

    suspend fun checkIsUserFollowing(othersUserId: String): Boolean{
        val followingRef = firestore.collection("users")
            .document(currentUid())
            .collection("following")
            .document(othersUserId)
            .get()
            .await()

        return followingRef.exists()
    }

    suspend fun getSuggestedUsers(limit: Int = 10): List<User> {
        val currentUid = currentUid()
        val followingSnapshot = firestore.collection("users")
            .document(currentUid)
            .collection("following")
            .get()
            .await()

        val followingIds = followingSnapshot.documents.map { it.id }.toMutableSet()
        followingIds.add(currentUid)

        val usersSnapshot = firestore.collection("users")
            .limit(30)
            .get()
            .await()

        return usersSnapshot
            .toObjects(User::class.java)
            .filter { it.id !in followingIds }
            .take(limit)
    }

    suspend fun getFollowers(userId: String): List<Followers> {
        return firestore.collection("users")
            .document(userId)
            .collection("followers")
            .get()
            .await()
            .toObjects(Followers::class.java)
    }

    suspend fun getFollowing(userId: String): List<Following> {
        return firestore.collection("users")
            .document(userId)
            .collection("following")
            .get()
            .await()
            .toObjects(Following::class.java)
    }


    suspend fun updateUserProfile(
        profileImage: String?,
        username: String,
        bio: String
    ) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")

        val updates = mutableMapOf<String, Any>(
            "userName" to username,
            "bio" to bio
        )
        if (!profileImage.isNullOrEmpty()) {
            updates["profileImage"] = profileImage
        }

        firestore.collection("users")
            .document(uid)
            .update(updates)
            .await()
    }


}