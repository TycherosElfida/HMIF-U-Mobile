package com.example.hmifu_mobile.model

/**
 * User domain model representing an HMIF member.
 * Maps to the Firestore 'users/{uid}' document structure.
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val nim: String = "",
    val nama: String = "",
    val angkatan: Int = 0,
    val roles: List<String> = emptyList(),
    val techStacks: List<String> = emptyList(),
    val profileImageUrl: String? = null
) {
    /**
     * Check if user has admin role
     */
    val isAdmin: Boolean
        get() = roles.contains("admin")
    
    /**
     * Check if user has member role
     */
    val isMember: Boolean
        get() = roles.contains("member")
    
    /**
     * Get display name (nama) or fallback to email prefix
     */
    val displayName: String
        get() = nama.ifBlank { email.substringBefore("@") }
}
