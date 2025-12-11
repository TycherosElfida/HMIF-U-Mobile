package com.example.hmifu_mobile

import android.app.Application
import com.example.hmifu_mobile.data.messaging.HmifMessagingService
import dagger.hilt.android.HiltAndroidApp

/**
 * HMIF U-Mobile Application class.
 * This is the entry point for Hilt dependency injection.
 */
@HiltAndroidApp
class HmifApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels
        HmifMessagingService.createNotificationChannels(this)
    }
}
