package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.DocumentDao
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import com.example.hmifu_mobile.data.local.entity.DocumentStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("documents")

    // Observing documents from Room (Source of Truth)
    fun getAllDocuments() = documentDao.getAllDocuments()
    fun getMyDocuments(uploaderId: String) = documentDao.getDocumentsByUploader(uploaderId)

    suspend fun syncDocuments() {
        try {
            val snapshot = collection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val documents = snapshot.documents.mapNotNull { it.toObject(DocumentEntity::class.java) }
            documentDao.insertDocuments(documents)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addDocument(document: DocumentEntity): Result<Unit> {
        return try {
            val docRef = collection.document()
            val newDoc = document.copy(id = docRef.id)
            docRef.set(newDoc).await()
            documentDao.insertDocuments(listOf(newDoc)) // Optimistic update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: String, status: DocumentStatus, rejectionReason: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status
            )
            if (rejectionReason != null) {
                updates["rejectionReason"] = rejectionReason
            }
            collection.document(id).update(updates).await()
            // Local update relies on sync or manual fetch if needed, but we can do optimistic update if we fetch first
            // For now, let sync handle it or simple UI refresh
            syncDocuments() 
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
