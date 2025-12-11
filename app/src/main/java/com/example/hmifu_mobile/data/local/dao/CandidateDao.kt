package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.CandidateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {
    @Query("SELECT * FROM candidates WHERE electionId = :electionId ORDER BY number ASC")
    fun getCandidates(electionId: String = "default_election"): Flow<List<CandidateEntity>>

    @Query("SELECT * FROM candidates WHERE id = :id")
    suspend fun getCandidateById(id: String): CandidateEntity?

    @Upsert
    suspend fun upsert(candidate: CandidateEntity)
    
    @Query("DELETE FROM candidates WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM candidates")
    suspend fun clearAll()
}
