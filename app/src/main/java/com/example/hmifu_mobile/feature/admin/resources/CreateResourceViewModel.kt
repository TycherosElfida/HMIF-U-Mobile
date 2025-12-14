package com.example.hmifu_mobile.feature.admin.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.example.hmifu_mobile.data.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateResourceUiState(
    val title: String = "",
    val subject: String = "",
    val semester: Int = 1,
    val year: Int = 2024,
    val type: String = "EXAM",
    val fileUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class CreateResourceViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateResourceUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onSubjectChange(value: String) = _uiState.update { it.copy(subject = value) }
    fun onSemesterChange(value: Int) = _uiState.update { it.copy(semester = value) }
    fun onYearChange(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(year = value.toIntOrNull() ?: 2024) }
        }
    }
    fun onTypeChange(value: String) = _uiState.update { it.copy(type = value) }
    fun onFileUrlChange(value: String) = _uiState.update { it.copy(fileUrl = value) }

    fun createResource(uploadedBy: String = "Admin") {
        val currentState = _uiState.value
        
        if (currentState.title.isBlank() || currentState.subject.isBlank() || currentState.fileUrl.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val resource = ResourceEntity(
                id = UUID.randomUUID().toString(),
                title = currentState.title,
                subject = currentState.subject,
                semester = currentState.semester,
                year = currentState.year,
                type = currentState.type.uppercase(),
                fileUrl = currentState.fileUrl,
                fileSize = 0, // Not available for text links
                uploadedBy = uploadedBy,
                uploadedAt = System.currentTimeMillis()
            )

            resourceRepository.addResource(resource)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
    fun resetSuccess() = _uiState.update { it.copy(success = false) }
}
