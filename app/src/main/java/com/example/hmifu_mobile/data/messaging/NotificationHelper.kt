package com.example.hmifu_mobile.data.messaging

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for managing FCM notifications.
 */
@Singleton
class NotificationHelper @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {


    /**
     * Subscribe to a topic for receiving notifications.
     */
    suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(topic)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unsubscribe from a topic.
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(topic)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current FCM token.
     */
    suspend fun getToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }


}
