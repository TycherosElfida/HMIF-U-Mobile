package com.example.hmifu_mobile.feature.certificates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import com.example.hmifu_mobile.data.repository.CertificateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val certificateRepository: CertificateRepository
) : ViewModel() {

    val certificates: StateFlow<List<CertificateEntity>> = certificateRepository
        .observeCertificates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        syncData()
    }

    private fun syncData() {
        viewModelScope.launch {
            certificateRepository.syncCertificates().collect {
                // Handle results if needed (error states etc)
            }
        }
    }
}
