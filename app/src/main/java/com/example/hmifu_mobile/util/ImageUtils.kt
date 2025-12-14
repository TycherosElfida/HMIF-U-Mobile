package com.example.hmifu_mobile.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

/**
 * Utility for handling image conversions and compression for Firestore Blobs.
 */
object ImageUtils {
    // Firestore limit is 1MB. We aim for 750KB to leave room for other data.
    private const val MAX_IMAGE_SIZE_BYTES = 750 * 1024 
    private const val MAX_DIMENSION = 800

    /**
     * Converts a Uri to a compressed ByteArray suitable for Firestore Blob.
     */
    fun uriToBytes(context: Context, uri: Uri): ByteArray? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            // 1. Resize if too large
            val (width, height) = getResizedDimensions(originalBitmap.width, originalBitmap.height)
            val scaledBitmap = if (width != originalBitmap.width || height != originalBitmap.height) {
                Bitmap.createScaledBitmap(originalBitmap, width, height, true)
            } else {
                originalBitmap
            }

            // 2. Compress to target size
            var quality = 90
            var stream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

            while (stream.toByteArray().size > MAX_IMAGE_SIZE_BYTES && quality > 10) {
                stream = ByteArrayOutputStream()
                quality -= 10
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            }

            return stream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Converts ByteArray back to Bitmap for display.
     */
    fun bytesToBitmap(bytes: ByteArray?): Bitmap? {
        if (bytes == null || bytes.isEmpty()) return null
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun getResizedDimensions(width: Int, height: Int): Pair<Int, Int> {
        if (width <= MAX_DIMENSION && height <= MAX_DIMENSION) return width to height

        val ratio = width.toFloat() / height.toFloat()
        return if (width > height) {
            MAX_DIMENSION to (MAX_DIMENSION / ratio).toInt()
        } else {
            (MAX_DIMENSION * ratio).toInt() to MAX_DIMENSION
        }
    }
}
