package com.example.hmifu_mobile.data.repository

import com.imagekit.android.ImageKit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for ImageKit.io operations
 *
 * Provides methods for constructing optimized image URLs with transformations.
 * Upload functionality requires backend authentication endpoint configuration.
 */
@Singleton
class ImageKitRepository @Inject constructor() {

    /**
     * Build an optimized image URL with optional transformations
     *
     * @param path The image path in ImageKit (e.g., "events/banner.jpg")
     * @param width Optional width constraint
     * @param height Optional height constraint
     * @param quality Image quality (1-100), defaults to 80
     * @return Fully constructed URL with transformations
     */
    fun getImageUrl(
        path: String,
        width: Float? = null,
        height: Float? = null,
        quality: Int = 80
    ): String {
        val urlBuilder = ImageKit.getInstance()
            .url(path = path)
            .quality(quality)
        
        width?.let { urlBuilder.width(it.toInt()) }
        height?.let { urlBuilder.height(it.toInt()) }
        
        return urlBuilder.create()
    }

    /**
     * Build an image URL with aspect ratio transformation
     *
     * @param path The image path in ImageKit
     * @param height Height constraint
     * @param aspectRatioWidth Aspect ratio width component
     * @param aspectRatioHeight Aspect ratio height component
     * @return Fully constructed URL with transformations
     */
    fun getImageUrlWithAspectRatio(
        path: String,
        height: Float,
        aspectRatioWidth: Int,
        aspectRatioHeight: Int
    ): String {
        return ImageKit.getInstance()
            .url(path = path)
            .height(height.toInt())
            .aspectRatio(aspectRatioWidth, aspectRatioHeight)
            .create()
    }

    /**
     * Build an image URL from absolute source URL
     *
     * @param src The full source URL of the image
     * @return URL with ImageKit transformations applied
     */
    fun getImageUrlFromSrc(src: String): String {
        return ImageKit.getInstance()
            .url(src = src)
            .create()
    }
}
