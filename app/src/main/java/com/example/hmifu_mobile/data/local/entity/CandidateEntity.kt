package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val number: Int, // Candidate number (Urut)
    val vision: String,
    val mission: String,
    val photoUrl: String? = null,
    val voteCount: Int = 0,
    val electionId: String = "default_election", // Support multiple elections in future
    val createdAt: Long = System.currentTimeMillis()
)
