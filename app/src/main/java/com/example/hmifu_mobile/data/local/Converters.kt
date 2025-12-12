package com.example.hmifu_mobile.data.local

import androidx.room.TypeConverter
import com.example.hmifu_mobile.data.local.entity.RegistrationStatus
import com.example.hmifu_mobile.data.local.entity.TransactionType

/**
 * Type converters for Room entities.
 */
class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return try {
            TransactionType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TransactionType.EXPENSE // Default fallback
        }
    }

    @TypeConverter
    fun fromRegistrationStatus(status: RegistrationStatus): String {
        return status.name
    }

    @TypeConverter
    fun toRegistrationStatus(value: String): RegistrationStatus {
        return try {
            RegistrationStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            RegistrationStatus.REGISTERED // Default fallback
        }
    }
}
