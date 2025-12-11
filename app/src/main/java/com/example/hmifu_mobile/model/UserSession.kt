package com.example.hmifu_mobile.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton to hold the current logged-in user session.
 * Provides easy access to user info and role checks throughout the app.
 */
@Singleton
class UserSession @Inject constructor() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    /**
     * Set the current user after login/registration
     */
    fun setUser(user: User?) {
        _currentUser.value = user
    }
    
    /**
     * Clear user session on logout
     */
    fun clear() {
        _currentUser.value = null
    }
    
    /**
     * Check if current user has admin role
     */
    fun isAdmin(): Boolean {
        return _currentUser.value?.isAdmin == true
    }
    
    /**
     * Get current user's name
     */
    fun getUserName(): String {
        return _currentUser.value?.nama ?: "User"
    }
    
    /**
     * Get current user's roles
     */
    fun getRoles(): List<String> {
        return _currentUser.value?.roles ?: emptyList()
    }
}
