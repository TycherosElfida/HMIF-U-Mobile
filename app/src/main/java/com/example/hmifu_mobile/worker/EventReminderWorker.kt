package com.example.hmifu_mobile.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hmifu_mobile.MainActivity
import com.example.hmifu_mobile.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.net.Uri

@HiltWorker
class EventReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getString("eventId") ?: return Result.failure()
        val eventTitle = inputData.getString("eventTitle") ?: "Upcoming Event"
        val eventTime = inputData.getLong("eventTime", 0L)

        showNotification(eventId, eventTitle)
        return Result.success()
    }

    private fun showNotification(eventId: String, title: String) {
        val channelId = "event_reminders"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Reminders"
            val descriptionText = "Notifications for upcoming events"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Deep link intent to open the app (potentially to the event detail)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // In a real app, you'd add data to navigate to the specific event
            // data = Uri.parse("hmif://event/$eventId") 
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 
            eventId.hashCode(), 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this resource exists or use a generic icon
            .setContentTitle("Event Reminder")
            .setContentText("'$title' starts in 2 hours!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
             if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(eventId.hashCode(), builder.build())
            }
        }
    }
}
