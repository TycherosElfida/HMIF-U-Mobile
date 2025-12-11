package com.example.hmifu_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.dao.UserDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.local.entity.UserEntity

/**
 * HMIF U-Mobile Room Database
 * Single source of truth for offline-first architecture.
 */
@Database(
    entities = [
        UserEntity::class,
        AnnouncementEntity::class,
        EventEntity::class,
        EventRegistrationEntity::class,
        com.example.hmifu_mobile.data.local.entity.TransactionEntity::class,
        com.example.hmifu_mobile.data.local.entity.DocumentEntity::class,
        com.example.hmifu_mobile.data.local.entity.CandidateEntity::class,
        com.example.hmifu_mobile.data.local.entity.VoteRecordEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class HmifDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun eventDao(): EventDao
    abstract fun eventRegistrationDao(): EventRegistrationDao
    abstract fun transactionDao(): com.example.hmifu_mobile.data.local.dao.TransactionDao
    abstract fun documentDao(): com.example.hmifu_mobile.data.local.dao.DocumentDao
    abstract fun candidateDao(): com.example.hmifu_mobile.data.local.dao.CandidateDao
    abstract fun voteRecordDao(): com.example.hmifu_mobile.data.local.dao.VoteRecordDao
}

