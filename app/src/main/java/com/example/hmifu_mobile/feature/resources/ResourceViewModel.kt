package com.example.hmifu_mobile.feature.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.example.hmifu_mobile.data.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResourcesUiState(
    val isLoading: Boolean = false,
    val selectedSemester: Int? = null,
    val selectedSubject: String? = null,
    val error: String? = null
)

@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    val resources: StateFlow<List<ResourceEntity>> = resourceRepository
        .observeAllResources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val subjects: StateFlow<List<String>> = resourceRepository
        .observeSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ResourcesUiState())
    val uiState: StateFlow<ResourcesUiState> = _uiState.asStateFlow()

    init {
        syncResources()
    }

    private fun syncResources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            resourceRepository.syncResources().collect { result ->
                result.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun selectSemester(semester: Int?) {
        _uiState.value = _uiState.value.copy(
            selectedSemester = semester,
            selectedSubject = null
        )
    }

    fun selectSubject(subject: String?) {
        _uiState.value = _uiState.value.copy(selectedSubject = subject)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
