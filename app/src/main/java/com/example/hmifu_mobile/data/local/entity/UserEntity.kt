package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for local Room database.
 * Mirrors the Firestore user document for offline access.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val nim: String,
    val name: String,
    val email: String,
    val angkatan: Int,
    val concentration: String = "",
    val roles: String = "[\"member\"]", // JSON array stored as String
    val techStack: String = "[]",       // JSON array stored as String
    val points: Int = 0,
    val membershipStatus: String = "active",
    val profilePhotoUrl: String? = null,
    val syncedAt: Long = System.currentTimeMillis()
)
