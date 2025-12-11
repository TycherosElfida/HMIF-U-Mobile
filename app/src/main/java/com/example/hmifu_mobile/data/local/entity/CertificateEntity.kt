package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "certificates",
    indices = [Index("issueDate")]
)
data class CertificateEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val eventTitle: String,
    val recipientName: String,
    val recipientNim: String,
    val fileUrl: String, // Firebase Storage URL or Local Path
    val issueDate: Long,
    val syncedAt: Long = 0
)
