package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.VoteRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoteRecordDao {
    @Query("SELECT * FROM vote_records WHERE electionId = :electionId")
    fun getVoteRecord(electionId: String = "default_election"): Flow<VoteRecordEntity?>

    @Upsert
    suspend fun saveVote(record: VoteRecordEntity)
}
