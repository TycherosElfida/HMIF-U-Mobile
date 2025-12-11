package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.model.Event
import com.example.hmifu_mobile.model.UserSession
import com.example.hmifu_mobile.repository.EventRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddEventUiState(
    val title: String = "",
    val description: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val isMultiDay: Boolean = false,
    val location: String = "",
    val imageUrl: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val imageUrlError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for Add Event screen
 */
@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = if (title.isBlank()) "Judul tidak boleh kosong" else null
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                descriptionError = if (description.isBlank()) "Deskripsi tidak boleh kosong" else null
            )
        }
    }

    fun onStartDateChange(date: Date?) {
        _uiState.update {
            it.copy(
                startDate = date,
                startDateError = null
            )
        }
    }

    fun onEndDateChange(date: Date?) {
        _uiState.update {
            val error = if (it.isMultiDay && date != null && it.startDate != null && date.before(it.startDate)) {
                "Tanggal selesai harus setelah tanggal mulai"
            } else null
            it.copy(
                endDate = date,
                endDateError = error
            )
        }
    }

    fun onMultiDayChange(isMultiDay: Boolean) {
        _uiState.update {
            it.copy(
                isMultiDay = isMultiDay,
                endDate = if (!isMultiDay) null else it.endDate,
                endDateError = null
            )
        }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun onImageUrlChange(url: String) {
        val error = if (url.isNotBlank() && !isValidImgurUrl(url)) {
            "Masukkan URL gambar Imgur yang valid"
        } else null
        _uiState.update { it.copy(imageUrl = url, imageUrlError = error) }
    }

    private fun isValidImgurUrl(url: String): Boolean {
        return url.startsWith("https://i.imgur.com/") || 
               url.startsWith("http://i.imgur.com/") ||
               url.startsWith("https://imgur.com/") ||
               url.contains("imgur.com")
    }

    fun addEvent() {
        val current = _uiState.value

        // Validate
        if (current.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul tidak boleh kosong") }
            return
        }

        if (current.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "Deskripsi tidak boleh kosong") }
            return
        }

        if (current.startDate == null) {
            _uiState.update { it.copy(startDateError = "Pilih tanggal mulai") }
            return
        }

        if (current.isMultiDay && current.endDate == null) {
            _uiState.update { it.copy(endDateError = "Pilih tanggal selesai") }
            return
        }

        if (current.isMultiDay && current.endDate != null && current.endDate.before(current.startDate)) {
            _uiState.update { it.copy(endDateError = "Tanggal selesai harus setelah tanggal mulai") }
            return
        }

        if (current.imageUrl.isNotBlank() && !isValidImgurUrl(current.imageUrl)) {
            _uiState.update { it.copy(imageUrlError = "Masukkan URL gambar Imgur yang valid") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val event = Event(
                title = current.title,
                description = current.description,
                startDate = Timestamp(current.startDate),
                endDate = if (current.isMultiDay && current.endDate != null) Timestamp(current.endDate) else null,
                isMultiDay = current.isMultiDay,
                location = current.location,
                imageUrl = current.imageUrl.ifBlank { null },
                createdBy = userSession.currentUser.value?.uid ?: ""
            )

            val result = eventRepository.addEvent(event)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Gagal menambahkan event"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
