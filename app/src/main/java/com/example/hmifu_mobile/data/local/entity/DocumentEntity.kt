package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // PROPOSAL, LPJ, EXPENSE_REPORT
    val status: String, // PENDING, APPROVED, REJECTED
    val description: String = "",
    val contentUrl: String? = null, // Link to PDF/Doc
    val senderId: String,
    val senderName: String = "",
    val createdAt: Long,
    val updatedAt: Long = createdAt
)

enum class DocumentType {
    PROPOSAL, LPJ, EXPENSE_REPORT
}

enum class DocumentStatus {
    PENDING, APPROVED, REJECTED
}
