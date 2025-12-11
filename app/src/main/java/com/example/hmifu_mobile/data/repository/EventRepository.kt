package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.entity.EventEntity
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
 * Repository for Event operations.
 * Implements offline-first pattern: UI observes Room, network syncs to Room.
 */
@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("events")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe all events from local database.
     */
    fun observeEvents(): Flow<List<EventEntity>> = eventDao.observeAll()

    /**
     * Observe upcoming events from local database.
     */
    fun observeUpcoming(): Flow<List<EventEntity>> = eventDao.observeUpcoming()

    /**
     * Observe events by category from local database.
     */
    fun observeByCategory(category: String): Flow<List<EventEntity>> =
        eventDao.observeByCategory(category)

    /**
     * Observe a single event by ID.
     */
    fun observeById(id: String): Flow<EventEntity?> = eventDao.observeById(id)

    /**
     * Sync events from Firestore to Room.
     */
    fun syncFromFirestore(): Flow<Result<Unit>> = callbackFlow {
        val listener = collection
            .orderBy("startTime", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val events = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            EventEntity(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "other",
                                location = doc.getString("location") ?: "",
                                isOnline = doc.getBoolean("isOnline") ?: false,
                                meetingUrl = doc.getString("meetingUrl"),
                                startTime = doc.getLong("startTime") ?: 0L,
                                endTime = doc.getLong("endTime") ?: 0L,
                                registrationDeadline = doc.getLong("registrationDeadline"),
                                maxParticipants = doc.getLong("maxParticipants")?.toInt(),
                                currentParticipants = doc.getLong("currentParticipants")?.toInt()
                                    ?: 0,
                                organizerId = doc.getString("organizerId") ?: "",
                                organizerName = doc.getString("organizerName") ?: "",
                                imageUrl = doc.getString("imageUrl"),
                                isPinned = doc.getBoolean("isPinned") ?: false,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    ioScope.launch {
                        eventDao.upsertAll(events)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get single event by ID.
     */
    suspend fun getById(id: String): EventEntity? = eventDao.getById(id)

    /**
     * Register for an event.
     */
    suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> {
        return try {
            // Add user to event's registrations subcollection
            collection.document(eventId)
                .collection("registrations")
                .document(userId)
                .set(mapOf("registeredAt" to System.currentTimeMillis()))
                .await()

            // Increment participant count
            firestore.runTransaction { transaction ->
                val eventRef = collection.document(eventId)
                val event = transaction.get(eventRef)
                val currentCount = event.getLong("currentParticipants") ?: 0
                transaction.update(eventRef, "currentParticipants", currentCount + 1)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
