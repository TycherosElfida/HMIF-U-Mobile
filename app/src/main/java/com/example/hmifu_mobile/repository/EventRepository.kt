package com.example.hmifu_mobile.repository

import android.util.Log
import com.example.hmifu_mobile.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing events in Firestore.
 */
@Singleton
class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val EVENTS_COLLECTION = "events"
    }

    /**
     * Add a new event to Firestore
     */
    suspend fun addEvent(event: Event): Result<Event> {
        return try {
            val docRef = firestore.collection(EVENTS_COLLECTION).document()
            val eventWithId = event.copy(
                id = docRef.id,
                createdAt = Timestamp.now()
            )
            docRef.set(eventWithId.toMap()).await()
            Result.success(eventWithId)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menambahkan event: ${e.localizedMessage}"))
        }
    }

    /**
     * Get all active events
     */
    fun getEvents(): Flow<List<Event>> = callbackFlow {
        val subscription = firestore.collection(EVENTS_COLLECTION)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventRepository", "Error fetching events: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Event.fromDocument(doc.id, it) }
                }?.sortedBy { it.startDate?.toDate() } ?: emptyList()
                
                trySend(events)
            }
        
        awaitClose { subscription.remove() }
    }

    /**
     * Delete an event (soft delete by setting isActive to false)
     */
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            firestore.collection(EVENTS_COLLECTION)
                .document(eventId)
                .update("isActive", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menghapus event: ${e.localizedMessage}"))
        }
    }
}
