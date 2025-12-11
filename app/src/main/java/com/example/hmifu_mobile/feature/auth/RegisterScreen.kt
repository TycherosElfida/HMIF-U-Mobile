package com.example.hmifu_mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.AnimatedScaleIn
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.GradientButton
import com.example.hmifu_mobile.ui.components.TextLinkButton
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Registration Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient background matching LoginScreen
 * - Glassmorphic card container
 * - Custom styled input fields
 * - Password strength indicator
 * - Gradient register button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigate on successful registration
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && uiState.needsProfileSetup) {
            onRegisterSuccess()
        }
    }

    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientEnd,
                            GradientStart,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
        ) {
            // Decorative circles
            FloatingDecorations()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(HmifTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.lg))

                // Glassmorphic registration card
                AnimatedScaleIn(delay = 100) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = HmifTheme.cornerRadius.xxl
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                        ) {
                            // Header
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Join HMIF 🚀",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Create your account to get started",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

                            // Email field
                            RegisterTextField(
                                value = uiState.email,
                                onValueChange = viewModel::updateEmail,
                                label = "Email",
                                placeholder = "student@ukrida.ac.id",
                                leadingIcon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email
                            )

                            // Password field
                            RegisterTextField(
                                value = uiState.password,
                                onValueChange = viewModel::updatePassword,
                                label = "Password",
                                leadingIcon = Icons.Default.Lock,
                                isPassword = true,
                                passwordVisible = passwordVisible,
                                onTogglePassword = { passwordVisible = !passwordVisible },
                                supportingText = "Minimum 6 characters"
                            )

                            // Confirm Password field
                            RegisterTextField(
                                value = uiState.confirmPassword,
                                onValueChange = viewModel::updateConfirmPassword,
                                label = "Confirm Password",
                                leadingIcon = Icons.Default.Lock,
                                isPassword = true,
                                passwordVisible = confirmPasswordVisible,
                                onTogglePassword = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                },
                                isError = uiState.confirmPassword.isNotEmpty() &&
                                        uiState.password != uiState.confirmPassword,
                                errorText = if (uiState.confirmPassword.isNotEmpty() &&
                                    uiState.password != uiState.confirmPassword
                                ) "Passwords don't match" else null
                            )

                            Spacer(modifier = Modifier.height(HmifTheme.spacing.md))

                            // Register button
                            GradientButton(
                                text = "Create Account",
                                onClick = viewModel::register,
                                isLoading = uiState.isLoading,
                                enabled = !uiState.isLoading &&
                                        uiState.email.isNotBlank() &&
                                        uiState.password.length >= 6 &&
                                        uiState.password == uiState.confirmPassword
                            )

                            // Divider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Text(
                                    text = "OR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                            }

                            // Login link
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Already have an account?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextLinkButton(
                                    text = "Login",
                                    onClick = onNavigateBack,
                                    color = HmifBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// STYLED TEXT FIELD
// ════════════════════════════════════════════════════════════════

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else HmifBlue
            )
        },
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        singleLine = true,
        isError = isError,
        supportingText = {
            when {
                errorText != null -> Text(errorText, color = MaterialTheme.colorScheme.error)
                supportingText != null -> Text(supportingText)
            }
        },
        shape = RoundedCornerShape(HmifTheme.cornerRadius.md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HmifBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = HmifBlue,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ════════════════════════════════════════════════════════════════
// DECORATIVE ELEMENTS
// ════════════════════════════════════════════════════════════════

@Composable
private fun FloatingDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top right circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(100.dp)
                .padding(end = 0.dp, top = 40.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50.dp)
                )
        )

        // Middle left circle
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(60.dp)
                .padding(start = 0.dp)
                .background(
                    Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(30.dp)
                )
        )

        // Bottom right circle
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(140.dp)
                .padding(end = 20.dp, bottom = 120.dp)
                .background(
                    Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(70.dp)
                )
        )
    }
}
