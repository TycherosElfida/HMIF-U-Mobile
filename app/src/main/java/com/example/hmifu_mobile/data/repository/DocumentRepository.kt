package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.DocumentDao
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("documents")

    fun getDocuments(): Flow<List<DocumentEntity>> = documentDao.getAllDocuments()

    suspend fun syncDocuments() {
        try {
            val snapshot = collection.get().await()
            val documents = snapshot.documents.mapNotNull { doc ->
                if (doc.exists()) {
                    DocumentEntity(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        type = doc.getString("type") ?: "PROPOSAL",
                        status = doc.getString("status") ?: "PENDING",
                        description = doc.getString("description") ?: "",
                        contentUrl = doc.getString("contentUrl"),
                        senderId = doc.getString("senderId") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                    )
                } else null
            }
            // Simple sync: clear and insert
            // In prod, use standard sync + diffing
            // For now, simpler to just upsert all
            documents.forEach { documentDao.upsert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateStatus(id: String, status: String): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            collection.document(id).update(
                mapOf(
                    "status" to status,
                    "updatedAt" to now
                )
            ).await()
            documentDao.updateStatus(id, status, now)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper to seed dummy data if needed
    suspend fun seedDummyData(senderId: String) {
        val dummy = DocumentEntity(
            id = java.util.UUID.randomUUID().toString(),
            title = "Proposal Futsal Cup 2025",
            type = "PROPOSAL",
            status = "PENDING",
            description = "Proposal anggaran dan konsep acara Futsal.",
            senderId = senderId,
            senderName = "Secretary A",
            createdAt = System.currentTimeMillis()
        )
        documentDao.upsert(dummy) // Local only for instant feedback
        // In real app, we would upload to Firestore here
    }
}
