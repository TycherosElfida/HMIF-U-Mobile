package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Resource entity for Bank Soal (Exam Repository).
 * Stores metadata for downloadable academic resources like past exam papers.
 */
@Entity(
    tableName = "resources",
    indices = [Index("subject"), Index("semester")]
)
data class ResourceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String,      // e.g., "Algoritma", "Basis Data"
    val semester: Int,        // 1-8
    val year: Int,            // e.g., 2024
    val type: String,         // "exam", "quiz", "assignment"
    val fileUrl: String,      // Firebase Storage URL
    val fileSize: Long = 0,   // bytes
    val uploadedBy: String,   // User ID of uploader
    val uploadedAt: Long,
    val downloadCount: Int = 0,
    val syncedAt: Long = 0
)
