package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Event operations.
 */
@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY isPinned DESC, startTime ASC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY isPinned DESC, startTime ASC")
    fun observeByCategory(category: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startTime > :now ORDER BY startTime ASC")
    fun observeUpcoming(now: Long = System.currentTimeMillis()): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventEntity?>

    @Upsert
    suspend fun upsertAll(events: List<EventEntity>)

    @Upsert
    suspend fun upsert(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}
