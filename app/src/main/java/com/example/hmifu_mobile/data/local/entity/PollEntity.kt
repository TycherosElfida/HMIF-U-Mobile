package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Poll entity for local database storage.
 * Represents a voting poll created by admins.
 */
@Entity(
    tableName = "polls",
    indices = [Index("createdAt")]
)
data class PollEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val creatorId: String,
    val creatorName: String,
    val options: String, // JSON array of options: [{"id": "1", "text": "Option A", "votes": 10}]
    val isActive: Boolean = true,
    val isMultipleChoice: Boolean = false,
    val expiresAt: Long? = null,
    val createdAt: Long,
    val totalVotes: Int = 0,
    val userVotedOptionId: String? = null, // Track if current user voted
    val syncedAt: Long = 0
)
