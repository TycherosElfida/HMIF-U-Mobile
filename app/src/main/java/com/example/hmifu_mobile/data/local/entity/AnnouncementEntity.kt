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
    val imageBlob: ByteArray? = null,
    val createdAt: Long,
    val updatedAt: Long = createdAt,
    val syncedAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnnouncementEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (category != other.category) return false
        if (authorId != other.authorId) return false
        if (authorName != other.authorName) return false
        if (isPinned != other.isPinned) return false
        if (imageBlob != null) {
            if (other.imageBlob == null) return false
            if (!imageBlob.contentEquals(other.imageBlob)) return false
        } else if (other.imageBlob != null) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (syncedAt != other.syncedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + isPinned.hashCode()
        result = 31 * result + (imageBlob?.contentHashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + syncedAt.hashCode()
        return result
    }
}

/**
 * Announcement categories.
 */
enum class AnnouncementCategory(val displayName: String, val emoji: String) {
    ACADEMIC("Academic", "📚"),
    EVENT("Event", "📅"),
    CAREER("Career", "💼"),
    COMPETITION("Competition", "🏆"),
    GENERAL("General", "📢");

    companion object {
        fun fromString(value: String): AnnouncementCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}

