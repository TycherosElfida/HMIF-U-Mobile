package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Announcement operations.
 */
@Dao
interface AnnouncementDao {

    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, createdAt DESC")
    fun observeAll(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE category = :category ORDER BY isPinned DESC, createdAt DESC")
    fun observeByCategory(category: String): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE id = :id")
    suspend fun getById(id: String): AnnouncementEntity?

    @Upsert
    suspend fun upsertAll(announcements: List<AnnouncementEntity>)

    @Upsert
    suspend fun upsert(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM announcements")
    suspend fun clearAll()
}
