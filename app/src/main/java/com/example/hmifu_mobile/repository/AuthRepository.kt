package com.example.hmifu_mobile.repository

import com.example.hmifu_mobile.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * Defines the contract for authentication functionality.
 */
interface AuthRepository {
    /**
     * Login with email and password
     * @param email User's email address
     * @param password User's password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun login(email: String, password: String): Result<User>
    
    /**
     * Logout the current user
     */
    suspend fun logout()
    
    /**
     * Get the currently authenticated user
     * @return User if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * Observe authentication state changes
     * @return Flow emitting User when authenticated, null when logged out
     */
    fun observeAuthState(): Flow<User?>
    
    /**
     * Check if user is currently authenticated
     */
    fun isAuthenticated(): Boolean
    
    /**
     * Send password reset email
     * @param email User's email address
     * @return Result indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Register a new user with email and password
     * @param email User's email address
     * @param password User's password
     * @param nama User's full name
     * @param nim Student ID number
     * @return Result containing User on success or Exception on failure
     */
    suspend fun register(
        email: String,
        password: String,
        nama: String,
        nim: String
    ): Result<User>
}
