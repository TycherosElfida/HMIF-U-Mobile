package com.example.hmifu_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.dao.CertificateDao
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.dao.PollDao
import com.example.hmifu_mobile.data.local.dao.ResourceDao
import com.example.hmifu_mobile.data.local.dao.UserDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.local.entity.PollEntity
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.example.hmifu_mobile.data.local.entity.UserEntity

/**
 * HMIF U-Mobile Room Database
 * Single source of truth for offline-first architecture.
 */
@Database(
    entities = [
        UserEntity::class,
        EventEntity::class,
        EventRegistrationEntity::class,
        AnnouncementEntity::class,
        CertificateEntity::class,
        PollEntity::class,
        ResourceEntity::class
    ],
    version = 5, // Bumped for schema changes - ImageKit migration
    exportSchema = false
)
abstract class HmifDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun eventRegistrationDao(): EventRegistrationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun certificateDao(): CertificateDao
    abstract fun pollDao(): PollDao
    abstract fun resourceDao(): ResourceDao
}
