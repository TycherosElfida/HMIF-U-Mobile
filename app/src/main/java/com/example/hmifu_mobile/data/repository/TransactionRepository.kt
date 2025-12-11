package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.TransactionDao
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
import com.example.hmifu_mobile.data.local.entity.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("transactions")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()
    
    fun observeTotalIncome(): Flow<Double?> = transactionDao.observeTotalIncome()
    
    fun observeTotalExpense(): Flow<Double?> = transactionDao.observeTotalExpense()

    fun syncFromFirestore(): Flow<Result<Unit>> = callbackFlow {
        val listener = collection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val transactions = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            TransactionEntity(
                                id = doc.id,
                                type = TransactionType.valueOf(doc.getString("type") ?: "EXPENSE"),
                                amount = doc.getDouble("amount") ?: 0.0,
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "Other",
                                date = doc.getLong("date") ?: System.currentTimeMillis(),
                                recordedByUserId = doc.getString("recordedByUserId") ?: "",
                                recordedByUserName = doc.getString("recordedByUserName") ?: "",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    ioScope.launch {
                        transactionDao.upsertAll(transactions)
                    }
                    trySend(Result.success(Unit))
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTransaction(
        type: TransactionType,
        amount: Double,
        description: String,
        category: String,
        user: UserEntity
    ): Result<Unit> {
        return try {
            val docRef = collection.document()
            val transaction = TransactionEntity(
                id = docRef.id,
                type = type,
                amount = amount,
                description = description,
                category = category,
                date = System.currentTimeMillis(),
                recordedByUserId = user.uid,
                recordedByUserName = user.name
            )

            val data = mapOf(
                "id" to transaction.id,
                "type" to transaction.type.name,
                "amount" to transaction.amount,
                "description" to transaction.description,
                "category" to transaction.category,
                "date" to transaction.date,
                "recordedByUserId" to transaction.recordedByUserId,
                "recordedByUserName" to transaction.recordedByUserName,
                "createdAt" to transaction.createdAt
            )
            
            docRef.set(data).await()
            // Local update strictly via sync, or optimistic update here if needed.
            // For now rely on sync listener for simplicity unless offline creation is critical immediately.
            // Actually, for offline-first we should insert locally too.
            transactionDao.upsert(transaction)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
