package com.example.hmifu_mobile.feature.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.repository.UserProfile
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserManagementUiState(
    val query: String = "",
    val users: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        // Initial load
        searchUsers("")
        
        // Debounce search
        viewModelScope.launch {
            _uiState
                .map { it.query }
                .distinctUntilChanged()
                .debounce(500)
                .collect { query ->
                    searchUsers(query)
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.searchUsers(query)
                .onSuccess { users ->
                    _uiState.update { it.copy(users = users, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun updateUserRole(uid: String, role: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.updateUserRole(uid, role)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Role updated to $role") }
                    // Refresh list to show new role
                    searchUsers(_uiState.value.query)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
