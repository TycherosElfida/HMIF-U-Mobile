package com.example.hmifu_mobile

import android.app.Application
import com.example.hmifu_mobile.data.messaging.HmifMessagingService
import com.example.hmifu_mobile.data.remote.imagekit.ImageKitConfig
import com.imagekit.android.ImageKit
import com.imagekit.android.entity.TransformationPosition
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

        // Initialize ImageKit SDK for image CDN and storage
        initializeImageKit()
    }

    private fun initializeImageKit() {
        ImageKit.init(
            context = applicationContext,
            publicKey = ImageKitConfig.PUBLIC_KEY,
            urlEndpoint = ImageKitConfig.URL_ENDPOINT,
            transformationPosition = TransformationPosition.PATH
        )
    }
}
