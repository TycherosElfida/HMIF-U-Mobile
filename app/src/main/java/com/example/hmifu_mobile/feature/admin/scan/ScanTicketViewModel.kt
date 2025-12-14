package com.example.hmifu_mobile.feature.admin.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.repository.EventRegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Success(val registration: EventRegistrationEntity) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class ScanTicketViewModel @Inject constructor(
    private val eventRegistrationRepository: EventRegistrationRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun onScanResult(content: String) {
        if (_scanState.value is ScanState.Loading) return

        viewModelScope.launch {
            _scanState.value = ScanState.Loading
            val result = eventRegistrationRepository.checkInUser(content)
            result.fold(
                onSuccess = { registration ->
                    _scanState.value = ScanState.Success(registration)
                },
                onFailure = { error ->
                    _scanState.value = ScanState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }
}
