package com.example.hmifu_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a financial transaction (Income/Expense).
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val description: String,
    val category: String, // e.g., "Event", "Merchandise", "Kas"
    val date: Long = System.currentTimeMillis(),
    val authorId: String,
    val authorName: String
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
