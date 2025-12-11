package com.example.hmifu_mobile.feature.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Member Card UI state.
 */
data class MemberCardUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userNim: String = "",
    val userAngkatan: String = "",
    val userRole: String = "member",
    val qrBitmap: Bitmap? = null,
    val timeRemaining: Int = 30,
    val currentCode: String = "",
    val errorMessage: String? = null
)

/**
 * ViewModel for Member Card with rotating QR code.
 */
@HiltViewModel
class MemberCardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberCardUiState())
    val uiState: StateFlow<MemberCardUiState> = _uiState.asStateFlow()

    private var userSecret: String = ""
    private var userId: String = ""

    init {
        loadUserAndStartQr()
    }

    private fun loadUserAndStartQr() {
        viewModelScope.launch {
            try {
                val result = userRepository.syncCurrentUser()
                result.onSuccess { profile ->
                    userId = profile.uid
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = profile.name,
                            userNim = profile.nim,
                            userAngkatan = profile.angkatan,
                            userRole = profile.role
                        )
                    }

                    // Get or generate user's TOTP secret
                    userSecret = getOrCreateSecret(profile.uid)

                    // Start QR code rotation
                    startQrRotation()
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private suspend fun getOrCreateSecret(userId: String): String {
        // Try to get existing secret from Firestore
        try {
            val doc = firestore.collection("users").document(userId).get().await()
            val existingSecret = doc.getString("qrSecret")

            if (!existingSecret.isNullOrBlank()) {
                return existingSecret
            }

            // Generate new secret and save
            val newSecret = TotpGenerator.generateSecret()
            firestore.collection("users")
                .document(userId)
                .update("qrSecret", newSecret)
                .await()

            return newSecret
        } catch (e: Exception) {
            // Fallback: generate temporary secret
            return TotpGenerator.generateSecret()
        }
    }

    private fun startQrRotation() {
        viewModelScope.launch {
            var lastCode = ""
            var lastQrBitmap: Bitmap? = null

            while (isActive) {
                val timeRemaining = TotpGenerator.getTimeRemaining()
                val code = TotpGenerator.generateCode(userSecret)

                // Only regenerate QR bitmap when code changes (every 30 seconds)
                if (code != lastCode) {
                    lastCode = code

                    // Use the start of the current 30-second window as timestamp
                    // This ensures the same timestamp for the entire window
                    val windowTimestamp = (System.currentTimeMillis() / 30000L) * 30000L

                    val qrData = QrCheckInData(
                        userId = userId,
                        eventId = "general", // For general member card
                        code = code,
                        timestamp = windowTimestamp
                    ).toQrString()

                    lastQrBitmap = generateQrBitmap(qrData)
                }

                _uiState.update {
                    it.copy(
                        qrBitmap = lastQrBitmap,
                        timeRemaining = timeRemaining,
                        currentCode = code
                    )
                }

                // Wait 1 second to update the countdown timer
                delay(1000)
            }
        }
    }

    private fun generateQrBitmap(data: String, size: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)

            val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
