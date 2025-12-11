package com.example.hmifu_mobile.data.remote.imagekit

/**
 * ImageKit.io configuration for HMIF U-Mobile
 *
 * This provides centralized configuration for the ImageKit SDK including
 * URL endpoint, public key, and folder organization.
 */
object ImageKitConfig {
    /**
     * ImageKit URL endpoint - used for constructing optimized image URLs
     */
    const val URL_ENDPOINT = "https://ik.imagekit.io/je6qogxg2"

    /**
     * Public API key for ImageKit SDK initialization
     * Note: Never include private key in client-side code
     */
    const val PUBLIC_KEY = "public_WaYEmO85R3IX7EMfd8NONkKPtH0="

    /**
     * Authentication endpoint for secure uploads
     * Replace with your actual backend endpoint that generates JWT tokens
     */
    const val AUTH_ENDPOINT = "http://www.yourserver.com/auth"

    /**
     * Folder paths for organizing different image types
     */
    object Folders {
        const val EVENTS = "/events/"
        const val PROFILES = "/profiles/"
        const val ANNOUNCEMENTS = "/announcements/"
        const val RESOURCES = "/resources/"
        const val CERTIFICATES = "/certificates/"
    }

    /**
     * Default image transformation quality (1-100)
     */
    const val DEFAULT_QUALITY = 80
}
