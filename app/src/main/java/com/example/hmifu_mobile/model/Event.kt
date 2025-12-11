package com.example.hmifu_mobile.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Event status based on dates
 */
enum class EventStatus(val label: String, val color: Color) {
    COMING_SOON("Coming Soon", Color(0xFF6366F1)),  // Indigo
    ON_GOING("On Going", Color(0xFF22C55E)),        // Green
    FINISHED("Finished", Color(0xFF6B7280))          // Gray
}

/**
 * Data class representing an event in HMIF.
 */
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val isMultiDay: Boolean = false,
    val location: String = "",
    val imageUrl: String? = null,
    val createdBy: String = "",
    val createdAt: Timestamp? = null,
    val isActive: Boolean = true
) {
    // Legacy support for single date
    val date: Timestamp? get() = startDate

    /**
     * Get event status based on current date
     */
    fun getStatus(): EventStatus {
        val now = Date()
        val start = startDate?.toDate()
        val end = if (isMultiDay) endDate?.toDate() else startDate?.toDate()

        return when {
            start == null -> EventStatus.COMING_SOON
            now.before(start) -> EventStatus.COMING_SOON
            end != null && now.after(end) -> EventStatus.FINISHED
            else -> EventStatus.ON_GOING
        }
    }

    /**
     * Convert to Firestore map for saving
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "description" to description,
        "startDate" to startDate,
        "endDate" to endDate,
        "isMultiDay" to isMultiDay,
        "location" to location,
        "imageUrl" to imageUrl,
        "createdBy" to createdBy,
        "createdAt" to createdAt,
        "isActive" to isActive
    )

    companion object {
        /**
         * Create Event from Firestore document
         */
        fun fromDocument(id: String, data: Map<String, Any?>): Event {
            return Event(
                id = id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                startDate = data["startDate"] as? Timestamp ?: data["date"] as? Timestamp,
                endDate = data["endDate"] as? Timestamp,
                isMultiDay = data["isMultiDay"] as? Boolean ?: false,
                location = data["location"] as? String ?: "",
                imageUrl = data["imageUrl"] as? String,
                createdBy = data["createdBy"] as? String ?: "",
                createdAt = data["createdAt"] as? Timestamp,
                isActive = data["isActive"] as? Boolean ?: true
            )
        }
    }
}
