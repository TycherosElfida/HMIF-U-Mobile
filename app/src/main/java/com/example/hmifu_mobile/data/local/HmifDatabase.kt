package com.example.hmifu_mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.dao.CertificateDao
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.dao.RegistrationDao
import com.example.hmifu_mobile.data.local.dao.UserDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
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
        EventEntity::class,
        RegistrationEntity::class,
        AnnouncementEntity::class,
        CertificateEntity::class
    ],
    version = 2, // Increment version for schema change
    exportSchema = false
)
abstract class HmifDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun certificateDao(): CertificateDao
}
