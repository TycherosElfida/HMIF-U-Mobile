package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a user's event registration.
 */
@Entity(
    tableName = "event_registrations",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("eventId"),
        Index("userId"),
        Index(value = ["eventId", "userId"], unique = true)
    ]
)
data class EventRegistrationEntity(
    @PrimaryKey
    val id: String,
    val eventId: String,
    val userId: String,
    val status: RegistrationStatus = RegistrationStatus.REGISTERED,
    val registeredAt: Long = System.currentTimeMillis(),
    val checkedInAt: Long? = null,
    val qrTicket: String? = null
)

/**
 * Registration status enum.
 */
enum class RegistrationStatus {
    REGISTERED,
    CHECKED_IN,
    CANCELLED,
    WAITLISTED
}
