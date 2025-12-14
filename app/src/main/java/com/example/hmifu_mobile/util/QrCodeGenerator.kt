package com.example.hmifu_mobile.util

import android.graphics.Bitmap
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeGenerator @Inject constructor() {

    fun generateQrCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
