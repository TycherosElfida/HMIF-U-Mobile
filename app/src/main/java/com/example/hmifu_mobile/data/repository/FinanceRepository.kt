package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.FinanceDao
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
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

/**
 * Repository for Finance operations.
 */
@Suppress("unused")
@Singleton
class FinanceRepository @Inject constructor(
    private val financeDao: FinanceDao,
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("transactions")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe all transactions.
     */
    fun observeAll() = financeDao.observeAll()

    /**
     * Observe total income.
     */
    fun observeTotalIncome() = financeDao.observeTotalIncome()

    /**
     * Observe total expense.
     */
    fun observeTotalExpense() = financeDao.observeTotalExpense()

    /**
     * Add a new transaction.
     */
    suspend fun addTransaction(
        amount: Double,
        type: TransactionType,
        description: String,
        category: String,
        authorId: String,
        authorName: String
    ): Result<Unit> {
        return try {
            val docRef = collection.document()
            val transaction = TransactionEntity(
                id = docRef.id,
                amount = amount,
                type = type,
                description = description,
                category = category,
                date = System.currentTimeMillis(),
                authorId = authorId,
                authorName = authorName
            )

            val data = mapOf(
                "amount" to transaction.amount,
                "type" to transaction.type.name,
                "description" to transaction.description,
                "category" to transaction.category,
                "date" to transaction.date,
                "authorId" to transaction.authorId,
                "authorName" to transaction.authorName
            )

            docRef.set(data).await()
            financeDao.upsert(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync transactions from Firestore.
     */
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
                                amount = doc.getDouble("amount") ?: 0.0,
                                type = TransactionType.valueOf(doc.getString("type") ?: "INCOME"),
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "Other",
                                date = doc.getLong("date") ?: 0L,
                                authorId = doc.getString("authorId") ?: "",
                                authorName = doc.getString("authorName") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    ioScope.launch {
                        financeDao.upsertAll(transactions)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }
}
