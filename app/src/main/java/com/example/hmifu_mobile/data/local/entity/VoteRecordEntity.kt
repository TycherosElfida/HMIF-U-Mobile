package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks if the current user has voted in an election.
 */
@Entity(tableName = "vote_records")
data class VoteRecordEntity(
    @PrimaryKey val electionId: String,
    val votedCandidateId: String?,
    val timestamp: Long = System.currentTimeMillis()
)
