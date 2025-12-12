package com.example.hmifu_mobile.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.GradientButton
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Edit Profile Screen - Premium 2025 Design
 *
 * Features:
 * - Glassmorphic form card
 * - Styled text fields with icons
 * - Gradient save button
 * - Loading skeleton state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Profile updated successfully!")
            onSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
            ) {
                // Form Card
                item {
                    StaggeredAnimatedItem(index = 0) {
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = HmifTheme.cornerRadius.lg
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                            ) {
                                Text(
                                    text = "Personal Information",
                                    fontWeight = FontWeight.SemiBold
                                )

                                // Photo URL
                                StyledTextField(
                                    value = uiState.photoUrl,
                                    onValueChange = viewModel::updatePhotoUrl,
                                    label = "Profile Picture URL",
                                    placeholder = "https://...",
                                    icon = Icons.Default.Image,
                                    iconColor = HmifPurple,
                                    enabled = !uiState.isSaving
                                )

                                // Name
                                StyledTextField(
                                    value = uiState.name,
                                    onValueChange = viewModel::updateName,
                                    label = "Full Name",
                                    placeholder = "Enter your full name",
                                    icon = Icons.Default.Person,
                                    iconColor = HmifBlue,
                                    enabled = !uiState.isSaving,
                                    isRequired = true
                                )

                                // NIM
                                StyledTextField(
                                    value = uiState.nim,
                                    onValueChange = viewModel::updateNim,
                                    label = "NIM",
                                    placeholder = "e.g., 13520123",
                                    icon = Icons.Default.Badge,
                                    iconColor = HmifOrange,
                                    enabled = !uiState.isSaving,
                                    isRequired = true
                                )

                                // Angkatan
                                StyledTextField(
                                    value = uiState.angkatan,
                                    onValueChange = viewModel::updateAngkatan,
                                    label = "Angkatan",
                                    placeholder = "e.g., 2020",
                                    icon = Icons.Default.Numbers,
                                    iconColor = GradientEnd,
                                    enabled = !uiState.isSaving,
                                    isRequired = true
                                )
                            }
                        }
                    }
                }

                // Academic Card
                item {
                    StaggeredAnimatedItem(index = 1) {
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = HmifTheme.cornerRadius.lg
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                            ) {
                                Text(
                                    text = "Academic & Skills",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                // Concentration
                                StyledTextField(
                                    value = uiState.concentration,
                                    onValueChange = viewModel::updateConcentration,
                                    label = "Concentration",
                                    placeholder = "e.g., Software Engineering",
                                    icon = Icons.Default.School,
                                    iconColor = HmifBlue,
                                    enabled = !uiState.isSaving
                                )

                                // Tech Stack
                                StyledTextField(
                                    value = uiState.techStack,
                                    onValueChange = viewModel::updateTechStack,
                                    label = "Tech Stack",
                                    placeholder = "e.g., Android, Kotlin, Flutter",
                                    icon = Icons.Default.Code,
                                    iconColor = HmifOrange,
                                    enabled = !uiState.isSaving,
                                    singleLine = false,
                                    minLines = 2
                                )
                            }
                        }
                    }
                }

                // Save Button
                item {
                    StaggeredAnimatedItem(index = 2) {
                        GradientButton(
                            text = "Save Changes",
                            onClick = viewModel::saveProfile,
                            enabled = uiState.isValid && !uiState.isSaving,
                            isLoading = uiState.isSaving
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
                }
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
    placeholder: String,
    icon: ImageVector,
    iconColor: Color,
    enabled: Boolean = true,
    isRequired: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(if (isRequired) "$label *" else label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        enabled = enabled,
        shape = RoundedCornerShape(HmifTheme.cornerRadius.md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HmifBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = HmifBlue
        )
    )
}

// ════════════════════════════════════════════════════════════════
// LOADING STATE
// ════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
    ) {
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 300.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 200.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 56.dp)
    }
}
