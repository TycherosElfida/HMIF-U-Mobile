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
    
    // Cache all users for client-side filtering
    private var _cachedUsers: List<UserProfile> = emptyList()

    init {
        // Initial load of all users
        loadAllUsers()
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.fetchAllUsers()
                .onSuccess { users ->
                     _cachedUsers = users
                    _uiState.update { it.copy(users = users, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        applyFilter(query)
    }

    private fun applyFilter(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(users = _cachedUsers) }
        } else {
            val lowerQuery = query.lowercase()
            val filtered = _cachedUsers.filter { user ->
                user.name.lowercase().contains(lowerQuery) || 
                user.nim.lowercase().contains(lowerQuery)
            }
            _uiState.update { it.copy(users = filtered) }
        }
    }

    fun updateUserRole(uid: String, role: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.updateUserRole(uid, role)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Role updated to $role") }
                    // Reload to ensure consistency, or update local cache
                    loadAllUsers() 
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
