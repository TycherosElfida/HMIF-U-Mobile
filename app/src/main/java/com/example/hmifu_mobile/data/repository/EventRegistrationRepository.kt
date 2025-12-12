package com.example.hmifu_mobile.data.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.local.entity.RegistrationStatus
import com.example.hmifu_mobile.worker.EventReminderWorker
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRegistrationRepository @Inject constructor(
    private val eventRegistrationDao: EventRegistrationDao,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {
    private val collection = firestore.collection("registrations")
    private val workManager = WorkManager.getInstance(context)

    fun isRegistered(eventId: String, userId: String): Flow<List<EventRegistrationEntity>> {
        // Using getRegistrationsByEvent and filtering or a specific query if available
        // For simplicity and reactivity, assume DAO has a specific flow or we filter a list flow
        // Revisiting DAO: getRegistrationsByEvent returns Flow<List>, getRegistrationsByUser returns Flow<List>
        // We need a specific check. Let's return Flow from Room.
        // The DAO doesn't have a flow return for specific (eventId, userId). 
        // We can observe the user's registrations and check containment.
        return eventRegistrationDao.getRegistrationsByUser(userId)
    }

    suspend fun registerEvent(event: EventEntity, user: FirebaseUser): Result<Unit> {
        return try {
            val registrationId = "${event.id}_${user.uid}"
            val registration = EventRegistrationEntity(
                id = registrationId,
                eventId = event.id,
                userId = user.uid,
                status = RegistrationStatus.REGISTERED,
                registeredAt = System.currentTimeMillis()
            )

            // Optimistic Local Update
            eventRegistrationDao.insert(registration)

            // Firestore Update
            val docRef = collection.document(registrationId)
            docRef.set(registration).await()

            // Schedule Reminder Notification (2 hours before event)
            scheduleReminder(event)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRegistration(event: EventEntity, userId: String): Result<Unit> {
        return try {
            val registrationId = "${event.id}_${userId}"
            
            // Optimistic Local Delete
            eventRegistrationDao.delete(registrationId)

            // Firestore Delete
            collection.document(registrationId).delete().await()

            // Cancel Worker
            workManager.cancelAllWorkByTag(registrationId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun scheduleReminder(event: EventEntity) {
        val currentTime = System.currentTimeMillis()
        val triggerTime = event.startTime - TimeUnit.HOURS.toMillis(2)
        
        if (triggerTime > currentTime) {
            val delay = triggerTime - currentTime
            
            val data = workDataOf(
                "eventId" to event.id,
                "eventTitle" to event.title,
                "eventTime" to event.startTime
            )

            val workRequest = OneTimeWorkRequestBuilder<EventReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("${event.id}_${event.startTime}") // Tag for cancellation reference, loosely unique
                .addTag("${event.id}") // More robust tag for cancellation
                .build()

            workManager.enqueue(workRequest)
        }
    }
}
