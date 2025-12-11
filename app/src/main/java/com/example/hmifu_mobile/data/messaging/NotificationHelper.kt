package com.example.hmifu_mobile.data.messaging

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
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
     * Check if notification permission is granted (Android 13+).
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not needed for older versions
        }
    }

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

    /**
     * Register current device token with the user's profile in Firestore.
     */
    suspend fun registerToken(): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(
            IllegalStateException("User not logged in")
        )

        return try {
            val token = getToken() ?: return Result.failure(
                IllegalStateException("Failed to get FCM token")
            )

            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()

            // Subscribe to default topics
            subscribeToTopic("announcements")
            subscribeToTopic("events")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unregister token (for logout).
     */
    suspend fun unregisterToken(): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.success(Unit)

        return try {
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", null)
                .await()

            // Unsubscribe from topics
            unsubscribeFromTopic("announcements")
            unsubscribeFromTopic("events")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
