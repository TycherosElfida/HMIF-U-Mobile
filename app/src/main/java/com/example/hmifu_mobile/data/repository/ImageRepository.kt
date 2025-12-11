package com.example.hmifu_mobile.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) {
    private val storageRef = storage.reference

    suspend fun uploadImage(uri: Uri, folder: String = "misc"): Result<String> {
        return try {
            val compressedBytes = compressImage(uri) ?: return Result.failure(Exception("Failed to compress image"))
            
            val filename = "${UUID.randomUUID()}.jpg"
            val ref = storageRef.child("$folder/$filename")
            
            ref.putBytes(compressedBytes).await()
            val downloadUrl = ref.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun compressImage(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return@withContext null

            // Resize if too large (max 1024x1024)
            val maxDimension = 1024
            val scale = if (originalBitmap.width > maxDimension || originalBitmap.height > maxDimension) {
                val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                if (ratio > 1) {
                    // Width is larger
                    maxDimension.toFloat() / originalBitmap.width.toFloat()
                } else {
                    maxDimension.toFloat() / originalBitmap.height.toFloat()
                }
            } else {
                1f
            }

            val resizedBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * scale).toInt(),
                    (originalBitmap.height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            // Compress to JPEG with 70% quality
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
