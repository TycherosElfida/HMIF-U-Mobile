package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Announcement operations.
 * Implements offline-first pattern: UI observes Room, network syncs to Room.
 */
@Suppress("unused")  // Public API methods kept for future use
@Singleton
class AnnouncementRepository @Inject constructor(
    private val announcementDao: AnnouncementDao,
    firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("announcements")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe all announcements from local database.
     */
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.observeAll()

    /**
     * Observe announcements by category from local database.
     */
    fun observeByCategory(category: String): Flow<List<AnnouncementEntity>> =
        announcementDao.observeByCategory(category)

    /**
     * Sync announcements from Firestore to Room.
     * Returns a Flow that emits the latest announcements.
     */
    fun syncFromFirestore(): Flow<Result<Unit>> = callbackFlow {
        val listener = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val announcements = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            AnnouncementEntity(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                body = doc.getString("body") ?: "",
                                category = doc.getString("category") ?: "general",
                                authorId = doc.getString("authorId") ?: "",
                                authorName = doc.getString("authorName") ?: "",
                                isPinned = doc.getBoolean("isPinned") ?: false,
                                imageBlob = doc.getBlob("imageBlob")?.toBytes(),
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    // Upsert to Room in IO scope
                    ioScope.launch {
                        announcementDao.upsertAll(announcements)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get single announcement by ID.
     */
    suspend fun getById(id: String): AnnouncementEntity? = announcementDao.getById(id)

    /**
     * Create a new announcement (admin only).
     */
    suspend fun create(announcement: AnnouncementEntity): Result<String> {
        return try {
            val docRef = collection.document()
            val data = mapOf(
                "title" to announcement.title,
                "body" to announcement.body,
                "category" to announcement.category,
                "authorId" to announcement.authorId,
                "authorName" to announcement.authorName,
                "isPinned" to announcement.isPinned,
                "imageBlob" to if (announcement.imageBlob != null) Blob.fromBytes(announcement.imageBlob) else null,
                "createdAt" to announcement.createdAt,
                "updatedAt" to announcement.updatedAt
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an announcement.
     */
    suspend fun update(announcement: AnnouncementEntity): Result<Unit> {
        return try {
            val data = mapOf(
                "title" to announcement.title,
                "body" to announcement.body,
                "category" to announcement.category,
                "isPinned" to announcement.isPinned,
                "imageBlob" to if (announcement.imageBlob != null) Blob.fromBytes(announcement.imageBlob) else null,
                "updatedAt" to System.currentTimeMillis()
            )
            collection.document(announcement.id).update(data).await()
            announcementDao.upsert(announcement.copy(updatedAt = System.currentTimeMillis()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete an announcement (admin only).
     */
    suspend fun delete(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            announcementDao.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

