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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.AnimatedScaleIn
import com.example.hmifu_mobile.ui.components.BounceIn
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.GradientButton
import com.example.hmifu_mobile.ui.components.TextLinkButton
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Login Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient background with floating elements
 * - Glassmorphic card container
 * - Animated logo with bounce effect
 * - Custom styled input fields
 * - Gradient login button
 * - Smooth error animations
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigate on successful login
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
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
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientStart,
                            GradientEnd,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
        ) {
            // Floating decorative circles (background)
            FloatingCircles()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(HmifTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))

                // Logo with bounce animation
                BounceIn(delay = 100) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // HMIF Logo placeholder
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(HmifTheme.cornerRadius.xl)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "HMIF",
                                style = MaterialTheme.typography.headlineMedium,
                                color = GradientStart,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(HmifTheme.spacing.xxl))

                // Glassmorphic login card
                AnimatedScaleIn(delay = 200) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = HmifTheme.cornerRadius.xxl
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
                        ) {
                            // Welcome text
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Welcome Back! 👋",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Login to your HMIF account",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

                            // Email field
                            StyledTextField(
                                value = uiState.email,
                                onValueChange = viewModel::updateEmail,
                                label = "Email",
                                leadingIcon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email
                            )

                            // Password field
                            StyledTextField(
                                value = uiState.password,
                                onValueChange = viewModel::updatePassword,
                                label = "Password",
                                leadingIcon = Icons.Default.Lock,
                                isPassword = true,
                                passwordVisible = passwordVisible,
                                onTogglePassword = { passwordVisible = !passwordVisible }
                            )

                            // Forgot password link
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextLinkButton(
                                    text = "Forgot Password?",
                                    onClick = { viewModel.sendPasswordReset() },
                                    color = HmifOrange
                                )
                            }

                            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

                            // Login button
                            GradientButton(
                                text = "Login",
                                onClick = viewModel::login,
                                isLoading = uiState.isLoading,
                                enabled = !uiState.isLoading
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

                            // Register link
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Don't have an account?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextLinkButton(
                                    text = "Create Account",
                                    onClick = onNavigateToRegister,
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
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = HmifBlue
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
        shape = RoundedCornerShape(HmifTheme.cornerRadius.md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HmifBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = HmifBlue
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ════════════════════════════════════════════════════════════════
// DECORATIVE FLOATING CIRCLES
// ════════════════════════════════════════════════════════════════

@Composable
private fun FloatingCircles() {
    // Decorative circles in background for depth
    Box(modifier = Modifier.fillMaxSize()) {
        // Top left circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(start = 0.dp, top = 60.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(60.dp)
                )
        )

        // Top right circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(80.dp)
                .padding(end = 20.dp, top = 100.dp)
                .background(
                    Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(40.dp)
                )
        )

        // Bottom left circle
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(150.dp)
                .padding(start = 0.dp, bottom = 100.dp)
                .background(
                    Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(75.dp)
                )
        )
    }
}
