package com.example.hmifu_mobile.data.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.hmifu_mobile.MainActivity
import com.example.hmifu_mobile.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging service for handling push notifications.
 */
class HmifMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID_ANNOUNCEMENTS = "announcements"
        const val CHANNEL_ID_EVENTS = "events"
        const val CHANNEL_ID_REMINDERS = "reminders"

        /**
         * Creates notification channels for the app.
         * Should be called during app initialization.
         */
        fun createNotificationChannels(context: Context) {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Announcements channel
            val announcementsChannel = NotificationChannel(
                CHANNEL_ID_ANNOUNCEMENTS,
                "Announcements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "HMIF announcements and news"
                enableVibration(true)
            }

            // Events channel
            val eventsChannel = NotificationChannel(
                CHANNEL_ID_EVENTS,
                "Events",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Event updates and registrations"
            }

            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Event reminders"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(announcementsChannel, eventsChannel, remindersChannel)
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to Firestore for the current user
        scope.launch {
            saveTokenToFirestore(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Get notification data
        val title = message.notification?.title ?: message.data["title"] ?: "HMIF U-Mobile"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "announcement"

        // Determine channel based on type
        val channelId = when (type) {
            "announcement" -> CHANNEL_ID_ANNOUNCEMENTS
            "event" -> CHANNEL_ID_EVENTS
            "reminder" -> CHANNEL_ID_REMINDERS
            else -> CHANNEL_ID_ANNOUNCEMENTS
        }

        showNotification(title, body, channelId, message.data)
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add data for deep linking
            data["id"]?.let { putExtra("notification_id", it) }
            data["type"]?.let { putExtra("notification_type", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    private fun saveTokenToFirestore(token: String) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: return

            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }
}
