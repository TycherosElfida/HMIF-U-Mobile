package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.PollEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PollDao {
    @Query("SELECT * FROM polls WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActivePolls(): Flow<List<PollEntity>>

    @Query("SELECT * FROM polls ORDER BY createdAt DESC")
    fun observeAllPolls(): Flow<List<PollEntity>>

    @Query("SELECT * FROM polls WHERE id = :pollId")
    fun observePoll(pollId: String): Flow<PollEntity?>

    @Query("SELECT * FROM polls WHERE id = :pollId")
    suspend fun getPollById(pollId: String): PollEntity?

    @Upsert
    suspend fun upsertPolls(polls: List<PollEntity>)

    @Upsert
    suspend fun upsertPoll(poll: PollEntity)

    @Query("DELETE FROM polls")
    suspend fun clearAll()
}
