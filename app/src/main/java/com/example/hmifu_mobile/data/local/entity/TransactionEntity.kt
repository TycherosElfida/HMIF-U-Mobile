package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val category: String, // e.g., "Event", "Merchandise", "Grant"
    val date: Long,
    val recordedByUserId: String,
    val recordedByUserName: String,
    val createdAt: Long = System.currentTimeMillis()
)
