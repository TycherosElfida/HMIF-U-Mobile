package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DocumentType {
    PROPOSAL, LPJ
}

enum class DocumentStatus {
    PENDING, APPROVED, REVISION, REJECTED
}

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val type: DocumentType = DocumentType.PROPOSAL,
    val url: String = "", // External link
    val status: DocumentStatus = DocumentStatus.PENDING,
    val uploaderId: String = "",
    val uploaderName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val rejectionReason: String? = null // Optional reason for Revision/Rejection
)
