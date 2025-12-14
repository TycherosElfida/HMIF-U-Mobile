package com.example.hmifu_mobile.feature.admin.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.example.hmifu_mobile.data.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageResourcesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ManageResourcesViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageResourcesUiState())
    val uiState = _uiState

    val resources: StateFlow<List<ResourceEntity>> = resourceRepository.observeAllResources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteResource(resourceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            resourceRepository.deleteResource(resourceId)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Resource deleted successfully" 
                        ) 
                    }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message 
                        ) 
                    }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
