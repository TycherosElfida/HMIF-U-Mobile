package com.example.hmifu_mobile.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.imagekit.android.ImageKit

/**
 * Composable for displaying images from ImageKit with automatic URL optimization
 *
 * This component constructs optimized image URLs using ImageKit's transformation
 * capabilities and displays them using Coil for efficient loading and caching.
 *
 * @param path ImageKit path to the image (e.g., "events/banner.jpg")
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the image
 * @param width Optional width transformation (in pixels)
 * @param height Optional height transformation (in pixels)
 * @param quality Image quality (1-100), defaults to 80
 * @param contentScale How to scale the image
 */
@Composable
fun ImageKitImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    width: Float? = null,
    height: Float? = null,
    quality: Int = 80,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    // Build optimized URL with transformations
    val imageUrl = remember(path, width, height, quality) {
        val urlBuilder = ImageKit.getInstance()
            .url(path = path)
            .quality(quality)

        // FIX: Convert Float to Int here
        width?.let { urlBuilder.width(it.toInt()) }
        height?.let { urlBuilder.height(it.toInt()) }

        urlBuilder.create()
    }

    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Composable for displaying images with aspect ratio transformation
 *
 * @param path ImageKit path to the image
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the image
 * @param height Height constraint
 * @param aspectRatioWidth Aspect ratio width component
 * @param aspectRatioHeight Aspect ratio height component
 * @param contentScale How to scale the image
 */
@Composable
fun ImageKitImageWithAspectRatio(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    height: Float = 400f,
    aspectRatioWidth: Int = 16,
    aspectRatioHeight: Int = 9,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    val imageUrl = remember(path, height, aspectRatioWidth, aspectRatioHeight) {
        ImageKit.getInstance()
            .url(path = path)
            // FIX: Ensure this is toInt() as well (looks like you had this one, but confirming)
            .height(height.toInt())
            .aspectRatio(aspectRatioWidth, aspectRatioHeight)
            .create()
    }

    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Composable for displaying event banner images with predefined settings
 *
 * @param path ImageKit path to the event banner
 * @param modifier Modifier for the image
 */
@Composable
fun EventBannerImage(
    path: String,
    modifier: Modifier = Modifier
) {
    ImageKitImageWithAspectRatio(
        path = path,
        contentDescription = "Event banner",
        modifier = modifier,
        height = 300f,
        aspectRatioWidth = 16,
        aspectRatioHeight = 9,
        contentScale = ContentScale.Crop
    )
}

/**
 * Composable for displaying user profile avatars with predefined settings
 *
 * @param path ImageKit path to the profile photo
 * @param modifier Modifier for the image
 * @param size Size of the avatar in dp
 */
@Composable
fun ProfileAvatarImage(
    path: String,
    modifier: Modifier = Modifier,
    size: Int = 80
) {
    ImageKitImage(
        path = path,
        contentDescription = "Profile photo",
        modifier = modifier.size(size.dp),
        width = (size * 2).toFloat(),
        height = (size * 2).toFloat(),
        quality = 90
    )
}
