package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE status = :status ORDER BY createdAt DESC")
    fun getDocumentsByStatus(status: String): Flow<List<DocumentEntity>>

    @Upsert
    suspend fun upsert(document: DocumentEntity)

    @Query("UPDATE documents SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, timestamp: Long)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM documents")
    suspend fun clearAll()
}
