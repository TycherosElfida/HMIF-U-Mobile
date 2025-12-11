package com.example.hmifu_mobile.feature.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.RegistrationStatus
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Result of a check-in attempt.
 */
sealed class CheckInResult {
    data class Success(val userName: String, val eventId: String) : CheckInResult()
    data class AlreadyCheckedIn(val userName: String) : CheckInResult()
    data class NotRegistered(val userId: String) : CheckInResult()
    data class InvalidCode(val reason: String) : CheckInResult()
    data class Error(val message: String) : CheckInResult()
}

/**
 * QR Scanner UI state.
 */
data class QrScannerUiState(
    val isScanning: Boolean = true,
    val isProcessing: Boolean = false,
    val lastResult: CheckInResult? = null,
    val totalCheckedIn: Int = 0,
    val errorMessage: String? = null
)

/**
 * ViewModel for QR Scanner (Admin check-in).
 */
@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val eventRegistrationDao: EventRegistrationDao,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    private var checkedInCount = 0
    private var currentEventId: String? = null

    /**
     * Sets the event context for check-in.
     */
    fun setEventContext(eventId: String) {
        currentEventId = eventId
        loadCheckedInCount(eventId)
    }

    private fun loadCheckedInCount(eventId: String) {
        viewModelScope.launch {
            eventRegistrationDao.getRegistrationsByEvent(eventId)
                .collect { registrations ->
                    checkedInCount = registrations.count {
                        it.status == RegistrationStatus.CHECKED_IN
                    }
                    _uiState.update { it.copy(totalCheckedIn = checkedInCount) }
                }
        }
    }

    /**
     * Processes a scanned QR code.
     */
    fun processQrCode(qrData: String) {
        if (_uiState.value.isProcessing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, isScanning = false) }

            try {
                val result = validateAndCheckIn(qrData)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        lastResult = result,
                        isScanning = result !is CheckInResult.Success
                    )
                }

                // Auto-resume scanning after delay for non-success cases
                if (result !is CheckInResult.Success) {
                    kotlinx.coroutines.delay(2000)
                    resumeScanning()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        lastResult = CheckInResult.Error(e.message ?: "Unknown error"),
                        isScanning = true
                    )
                }
            }
        }
    }

    private suspend fun validateAndCheckIn(qrData: String): CheckInResult {
        // Parse QR data
        val checkInData = QrCheckInData.fromQrString(qrData)
            ?: return CheckInResult.InvalidCode("Invalid QR format")

        val userId = checkInData.userId
        val eventId = currentEventId ?: checkInData.eventId

        // Validate TOTP code
        val userSecret = getUserSecret(userId)
            ?: return CheckInResult.InvalidCode("User not found")

        val isValidCode = TotpGenerator.validateCode(
            secret = userSecret,
            code = checkInData.code,
            windowSize = 2 // Allow 2 windows (60 seconds) tolerance
        )

        if (!isValidCode) {
            return CheckInResult.InvalidCode("Expired or invalid code")
        }

        // Check registration
        val registration = eventRegistrationDao.getRegistration(eventId, userId)
            ?: return CheckInResult.NotRegistered(userId)

        // Check if already checked in
        if (registration.status == RegistrationStatus.CHECKED_IN) {
            val userName = getUserName(userId)
            return CheckInResult.AlreadyCheckedIn(userName)
        }

        // Perform check-in
        eventRegistrationDao.checkIn(
            registrationId = registration.id,
            timestamp = System.currentTimeMillis()
        )

        // Update Firestore
        try {
            firestore.collection("event_registrations")
                .document(registration.id)
                .update(
                    mapOf(
                        "status" to RegistrationStatus.CHECKED_IN.name,
                        "checkedInAt" to System.currentTimeMillis()
                    )
                ).await()
        } catch (e: Exception) {
            // Log but don't fail - local DB is updated
        }

        val userName = getUserName(userId)
        return CheckInResult.Success(userName, eventId)
    }

    private suspend fun getUserSecret(userId: String): String? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.getString("qrSecret")
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getUserName(userId: String): String {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.getString("name") ?: "Unknown User"
        } catch (e: Exception) {
            "Unknown User"
        }
    }

    /**
     * Resumes scanning after a check-in.
     */
    fun resumeScanning() {
        _uiState.update { it.copy(isScanning = true, lastResult = null) }
    }

    /**
     * Dismisses the last result.
     */
    fun dismissResult() {
        _uiState.update { it.copy(lastResult = null, isScanning = true) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
