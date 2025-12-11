package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Event entity for local Room database.
 * Mirrors the Firestore events collection.
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,   // workshop, seminar, competition, social, meeting
    val location: String,
    val isOnline: Boolean = false,
    val meetingUrl: String? = null,
    val startTime: Long,
    val endTime: Long,
    val registrationDeadline: Long? = null,
    val maxParticipants: Int? = null,
    val currentParticipants: Int = 0,
    val organizerId: String,
    val organizerName: String = "",
    val imageUrl: String? = null,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long = createdAt,
    val syncedAt: Long = System.currentTimeMillis()
)

/**
 * Event categories.
 */
enum class EventCategory(val displayName: String, val emoji: String) {
    WORKSHOP("Workshop", "🛠️"),
    SEMINAR("Seminar", "📚"),
    COMPETITION("Competition", "🏆"),
    SOCIAL("Social", "🎉"),
    MEETING("Meeting", "📋"),
    OTHER("Other", "📌");

    companion object {
        fun fromString(value: String): EventCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

/**
 * Event status based on time.
 */
enum class EventStatus {
    UPCOMING,
    ONGOING,
    ENDED;

    companion object {
        fun fromEvent(event: EventEntity): EventStatus {
            val now = System.currentTimeMillis()
            return when {
                now < event.startTime -> UPCOMING
                now in event.startTime..event.endTime -> ONGOING
                else -> ENDED
            }
        }
    }
}
