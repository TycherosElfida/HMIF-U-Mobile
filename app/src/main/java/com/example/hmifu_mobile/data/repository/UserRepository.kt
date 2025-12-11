package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.UserDao
import com.example.hmifu_mobile.data.local.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User profile data class for UI.
 */
data class UserProfile(
    val uid: String,
    val email: String,
    val name: String = "",
    val nim: String = "",
    val angkatan: String = "",
    val concentration: String = "",
    val techStack: String = "",
    val photoUrl: String? = null,
    val role: String = "member"  // member, admin, moderator
)

/**
 * Repository for User profile operations.
 */
@Suppress("unused")  // Public API methods kept for future use
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    /**
     * Get current user's UID.
     */
    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    /**
     * Get current user's email.
     */
    val currentUserEmail: String?
        get() = firebaseAuth.currentUser?.email

    /**
     * Observe current user from local database.
     */
    fun observeCurrentUser(): Flow<UserEntity?> {
        val uid = currentUserId ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.observeById(uid)
    }

    /**
     * Sync current user profile from Firestore to Room.
     */
    suspend fun syncCurrentUser(): Result<UserProfile> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))

        return try {
            val doc = usersCollection.document(uid).get().await()

            if (doc.exists()) {
                val userEntity = UserEntity(
                    uid = uid,
                    email = currentUserEmail ?: "",
                    name = doc.getString("name") ?: "",
                    nim = doc.getString("nim") ?: "",
                    angkatan = doc.getString("angkatan") ?: "",
                    concentration = doc.getString("concentration") ?: "",
                    techStack = doc.getString("techStack") ?: "",
                    photoUrl = doc.getString("photoUrl"),
                    role = doc.getString("role") ?: "member",
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )

                userDao.upsert(userEntity)

                Result.success(userEntity.toProfile())
            } else {
                // Create default profile
                val profile = UserProfile(
                    uid = uid,
                    email = currentUserEmail ?: ""
                )
                Result.success(profile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user profile in Firestore and Room.
     */
    suspend fun updateProfile(profile: UserProfile): Result<Unit> {
        return try {
            val data = mapOf(
                "name" to profile.name,
                "nim" to profile.nim,
                "angkatan" to profile.angkatan,
                "concentration" to profile.concentration,
                "techStack" to profile.techStack,
                "photoUrl" to profile.photoUrl,
                "role" to profile.role,
                "updatedAt" to System.currentTimeMillis()
            )

            usersCollection.document(profile.uid).set(data).await()

            // Update local cache
            val entity = UserEntity(
                uid = profile.uid,
                email = profile.email,
                name = profile.name,
                nim = profile.nim,
                angkatan = profile.angkatan,
                concentration = profile.concentration,
                techStack = profile.techStack,
                photoUrl = profile.photoUrl,
                role = profile.role,
                updatedAt = System.currentTimeMillis()
            )
            userDao.upsert(entity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get user profile by ID.
     */
    suspend fun getProfile(uid: String): Result<UserProfile> {
        return try {
            val doc = usersCollection.document(uid).get().await()

            if (doc.exists()) {
                val profile = UserProfile(
                    uid = uid,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    nim = doc.getString("nim") ?: "",
                    angkatan = doc.getString("angkatan") ?: "",
                    concentration = doc.getString("concentration") ?: "",
                    techStack = doc.getString("techStack") ?: "",
                    photoUrl = doc.getString("photoUrl"),
                    role = doc.getString("role") ?: "member"
                )
                Result.success(profile)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search users by name or email.
     */
    suspend fun searchUsers(query: String): Result<List<UserProfile>> {
        return try {
            // Simple search implementation (Firestore is limited in search)
            // We'll fetch by name and email (startAt/endAt) or use a composite index if available.
            // For now, let's fetch recent users or just support prefix search on 'name'.
            // Note: Case sensitivity is an issue with Firestore.
            
            // A more robust app uses Algolia/Typesense. For this MVP, we might fetch all (if small) or just some.
            // Let's implement prefix search on 'name'.
            
            val snapshot = usersCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(50)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                if (doc.exists()) {
                    UserProfile(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        name = doc.getString("name") ?: "",
                        nim = doc.getString("nim") ?: "",
                        angkatan = doc.getString("angkatan") ?: "",
                        concentration = doc.getString("concentration") ?: "",
                        techStack = doc.getString("techStack") ?: "",
                        photoUrl = doc.getString("photoUrl"),
                        role = doc.getString("role") ?: "member"
                    )
                } else null
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a user's role (Admin/President only).
     */
    suspend fun updateUserRole(uid: String, newRole: String): Result<Unit> {
        return try {
            usersCollection.document(uid).update("role", newRole).await()
            // We should also update local cache if it exists, or just rely on network for this admin feature.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserEntity.toProfile(): UserProfile = UserProfile(
        uid = uid,
        email = email,
        name = name,
        nim = nim,
        angkatan = angkatan,
        concentration = concentration,
        techStack = techStack,
        photoUrl = photoUrl,
        role = role
    )
}
