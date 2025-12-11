package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Announcement entity for local Room database.
 * Mirrors the Firestore announcements collection.
 */
@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val category: String,  // academic, event, career, competition, general
    val authorId: String,
    val authorName: String = "",
    val isPinned: Boolean = false,
    val attachmentUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long = createdAt,
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Announcement categories.
 */
enum class AnnouncementCategory(val displayName: String) {
    ACADEMIC("Academic"),
    EVENT("Event"),
    CAREER("Career"),
    COMPETITION("Competition"),
    GENERAL("General");

    companion object {
        fun fromString(value: String): AnnouncementCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}
