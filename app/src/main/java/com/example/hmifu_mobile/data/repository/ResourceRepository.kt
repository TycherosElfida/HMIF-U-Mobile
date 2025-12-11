package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.ResourceDao
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Bank Soal (Exam Repository) operations.
 * Syncs resources from Firestore to Room.
 */
@Suppress("unused") // Public API methods kept for future use
@Singleton
class ResourceRepository @Inject constructor(
    private val resourceDao: ResourceDao,
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("resources")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe all resources from Room.
     */
    fun observeAllResources(): Flow<List<ResourceEntity>> = resourceDao.observeAllResources()

    /**
     * Observe resources by subject.
     */
    fun observeBySubject(subject: String): Flow<List<ResourceEntity>> =
        resourceDao.observeBySubject(subject)

    /**
     * Observe resources by semester.
     */
    fun observeBySemester(semester: Int): Flow<List<ResourceEntity>> =
        resourceDao.observeBySemester(semester)

    /**
     * Observe available subjects.
     */
    fun observeSubjects(): Flow<List<String>> = resourceDao.observeSubjects()

    /**
     * Sync resources from Firestore to Room.
     */
    fun syncResources(): Flow<Result<Unit>> = callbackFlow {
        val listener = collection
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val resources = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            ResourceEntity(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                subject = doc.getString("subject") ?: "",
                                semester = doc.getLong("semester")?.toInt() ?: 1,
                                year = doc.getLong("year")?.toInt() ?: 2024,
                                type = doc.getString("type") ?: "exam",
                                fileUrl = doc.getString("fileUrl") ?: "",
                                fileSize = doc.getLong("fileSize") ?: 0,
                                uploadedBy = doc.getString("uploadedBy") ?: "",
                                uploadedAt = doc.getLong("uploadedAt")
                                    ?: System.currentTimeMillis(),
                                downloadCount = doc.getLong("downloadCount")?.toInt() ?: 0,
                                syncedAt = System.currentTimeMillis()
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }

                    ioScope.launch {
                        resourceDao.upsertResources(resources)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }
}
