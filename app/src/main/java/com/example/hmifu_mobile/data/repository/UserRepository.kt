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
    val photoBlob: ByteArray? = null,
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
                    photoBlob = doc.getBlob("photoBlob")?.toBytes(),
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
                "photoBlob" to if (profile.photoBlob != null) com.google.firebase.firestore.Blob.fromBytes(profile.photoBlob) else null,
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
                photoBlob = profile.photoBlob,
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
                    photoBlob = doc.getBlob("photoBlob")?.toBytes(),
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
     * Search users by name (prefix match).
     */
    /**
     * Search users by name (prefix match).
     * Keeping this for reference, but prefer fetchAllUsers for client-side flexibility.
     */
    suspend fun searchUsers(query: String): Result<List<UserProfile>> {
        return try {
            val q = if (query.isBlank()) {
                usersCollection.limit(20)
            } else {
                usersCollection
                    .orderBy("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .limit(20)
            }

            val snapshot = q.get().await()
            val users = snapshot.documents.mapNotNull { doc ->
                if (!doc.exists()) return@mapNotNull null
                UserProfile(
                    uid = doc.id,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    nim = doc.getString("nim") ?: "",
                    angkatan = doc.getString("angkatan") ?: "",
                    concentration = doc.getString("concentration") ?: "",
                    techStack = doc.getString("techStack") ?: "",
                    photoBlob = doc.getBlob("photoBlob")?.toBytes(),
                    role = doc.getString("role") ?: "member"
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch all users (limit 1000) for client-side filtering.
     */
    suspend fun fetchAllUsers(): Result<List<UserProfile>> {
        return try {
            val snapshot = usersCollection
                .limit(1000)
                .get()
                .await()
            
            val users = snapshot.documents.mapNotNull { doc ->
                if (!doc.exists()) return@mapNotNull null
                UserProfile(
                    uid = doc.id,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    nim = doc.getString("nim") ?: "",
                    angkatan = doc.getString("angkatan") ?: "",
                    concentration = doc.getString("concentration") ?: "",
                    techStack = doc.getString("techStack") ?: "",
                    photoBlob = doc.getBlob("photoBlob")?.toBytes(),
                    role = doc.getString("role") ?: "member"
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a user's role.
     */
    suspend fun updateUserRole(uid: String, role: String): Result<Unit> {
        return try {
            usersCollection.document(uid).update("role", role).await()
            
            // Also update local cache if it exists in DB
            val localUser = userDao.getUser(uid)
            if (localUser != null) {
                userDao.upsert(localUser.copy(role = role))
            }
            
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
        photoBlob = photoBlob,
        role = role
    )
}
