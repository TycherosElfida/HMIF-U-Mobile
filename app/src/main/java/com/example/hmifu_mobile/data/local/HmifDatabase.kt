package com.example.hmifu_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hmifu_mobile.data.local.dao.UserDao
import com.example.hmifu_mobile.data.local.entity.UserEntity

/**
 * HMIF U-Mobile Room Database
 * Single source of truth for offline-first architecture.
 */
@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HmifDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
