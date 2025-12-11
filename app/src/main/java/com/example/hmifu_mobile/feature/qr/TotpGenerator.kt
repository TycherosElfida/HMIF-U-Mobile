package com.example.hmifu_mobile.feature.qr

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor

/**
 * Time-based One-Time Password (TOTP) Generator.
 * Generates secure rotating codes for QR-based check-in.
 *
 * Security: Uses HMAC-SHA256 with 30-second time windows.
 */
object TotpGenerator {

    private const val TIME_STEP_SECONDS = 30L
    private const val CODE_DIGITS = 6
    private const val ALGORITHM = "HmacSHA256"

    /**
     * Generates a new secret key for a user.
     * This should be stored securely in user's profile.
     */
    fun generateSecret(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates a TOTP code based on secret and current time.
     *
     * @param secret The user's secret key (hex string)
     * @param timeMillis Current time in milliseconds
     * @return 6-digit TOTP code as string
     */
    fun generateCode(secret: String, timeMillis: Long = System.currentTimeMillis()): String {
        val counter = floor(timeMillis / 1000.0 / TIME_STEP_SECONDS).toLong()
        return generateHotp(secret, counter)
    }

    /**
     * Validates a TOTP code with a tolerance window.
     *
     * @param secret The user's secret key
     * @param code The code to validate
     * @param windowSize Number of time steps to check (before and after current)
     * @return true if code is valid within the window
     */
    fun validateCode(
        secret: String,
        code: String,
        windowSize: Int = 1,
        timeMillis: Long = System.currentTimeMillis()
    ): Boolean {
        val currentCounter = floor(timeMillis / 1000.0 / TIME_STEP_SECONDS).toLong()

        // Check current and adjacent windows for clock drift tolerance
        for (offset in -windowSize..windowSize) {
            val checkCounter = currentCounter + offset
            val expectedCode = generateHotp(secret, checkCounter)
            if (expectedCode == code) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the remaining seconds until the next code rotation.
     */
    fun getTimeRemaining(timeMillis: Long = System.currentTimeMillis()): Int {
        val elapsed = (timeMillis / 1000) % TIME_STEP_SECONDS
        return (TIME_STEP_SECONDS - elapsed).toInt()
    }

    /**
     * Generates HMAC-based One-Time Password.
     */
    private fun generateHotp(secret: String, counter: Long): String {
        val secretBytes = hexStringToByteArray(secret)
        val counterBytes = longToBytes(counter)

        val mac = Mac.getInstance(ALGORITHM)
        mac.init(SecretKeySpec(secretBytes, ALGORITHM))
        val hash = mac.doFinal(counterBytes)

        // Dynamic truncation (RFC 4226)
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % 1_000_000 // 6 digits
        return otp.toString().padStart(CODE_DIGITS, '0')
    }

    private fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) +
                    Character.digit(hex[i + 1], 16)).toByte()
        }
        return data
    }

    private fun longToBytes(value: Long): ByteArray {
        val result = ByteArray(8)
        var temp = value
        for (i in 7 downTo 0) {
            result[i] = (temp and 0xFF).toByte()
            temp = temp shr 8
        }
        return result
    }
}

/**
 * QR code data model for check-in.
 */
data class QrCheckInData(
    val userId: String,
    val eventId: String,
    val code: String,
    val timestamp: Long
) {
    /**
     * Serializes to QR-friendly string format.
     */
    fun toQrString(): String {
        return "HMIF:$userId:$eventId:$code:$timestamp"
    }

    companion object {
        /**
         * Parses QR string back to data object.
         */
        fun fromQrString(data: String): QrCheckInData? {
            return try {
                val parts = data.split(":")
                if (parts.size != 5 || parts[0] != "HMIF") return null

                QrCheckInData(
                    userId = parts[1],
                    eventId = parts[2],
                    code = parts[3],
                    timestamp = parts[4].toLong()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
