package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for financial transactions.
 */
@Dao
interface FinanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun observeByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun observeTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun observeTotalExpense(): Flow<Double?>

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
