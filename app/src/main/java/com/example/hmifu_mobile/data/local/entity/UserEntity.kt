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
    val email: String = "",
    val name: String = "",
    val nim: String = "",
    val angkatan: String = "",
    val concentration: String = "",
    val techStack: String = "",
    val photoBlob: ByteArray? = null,
    val role: String = ROLE_MEMBER,
    val points: Int = 0,
    val membershipStatus: String = "active",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_MODERATOR = "moderator"
        const val ROLE_STAFF = "staff"
        const val ROLE_MEMBER = "member"
    }
}
