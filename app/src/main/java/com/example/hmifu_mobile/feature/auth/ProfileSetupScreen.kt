package com.example.hmifu_mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.GradientButton
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Profile Setup Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient background
 * - Glassmorphic form card
 * - Styled inputs with icons
 * - Dropdown menus for selections
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onProfileComplete: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var concentrationExpanded by remember { mutableStateOf(false) }
    var angkatanExpanded by remember { mutableStateOf(false) }

    val concentrationOptions = listOf(
        "Software Engineering",
        "Data Science",
        "Network & Security",
        "Game Development",
        "Mobile Development"
    )

    val angkatanOptions = (2020..2025).map { it.toString() }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onProfileComplete()
        }
    }

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
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(HmifTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.xxl))

                // Header
                Text(
                    text = "👋",
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(HmifTheme.spacing.md))

                Text(
                    text = "Complete Your Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Tell us about yourself",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(HmifTheme.spacing.xxl))

                // Form Card
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = HmifTheme.cornerRadius.xxl
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                    ) {
                        // Full Name
                        SetupTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            label = "Full Name",
                            placeholder = "John Doe",
                            icon = Icons.Default.Person,
                            iconColor = HmifBlue
                        )

                        // NIM
                        SetupTextField(
                            value = uiState.nim,
                            onValueChange = viewModel::updateNim,
                            label = "NIM (Student ID)",
                            placeholder = "2024001234",
                            icon = Icons.Default.Badge,
                            iconColor = HmifOrange,
                            keyboardType = KeyboardType.Number
                        )

                        // Angkatan dropdown
                        ExposedDropdownMenuBox(
                            expanded = angkatanExpanded,
                            onExpandedChange = { angkatanExpanded = it }
                        ) {
                            SetupTextField(
                                value = uiState.angkatan,
                                onValueChange = {},
                                label = "Batch Year (Angkatan)",
                                placeholder = "Select year",
                                icon = Icons.Default.School,
                                iconColor = HmifPurple,
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = angkatanExpanded)
                                },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = angkatanExpanded,
                                onDismissRequest = { angkatanExpanded = false }
                            ) {
                                angkatanOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            viewModel.updateAngkatan(option)
                                            angkatanExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Concentration dropdown
                        ExposedDropdownMenuBox(
                            expanded = concentrationExpanded,
                            onExpandedChange = { concentrationExpanded = it }
                        ) {
                            SetupTextField(
                                value = uiState.concentration,
                                onValueChange = {},
                                label = "Concentration",
                                placeholder = "Select concentration",
                                icon = Icons.Default.Code,
                                iconColor = GradientEnd,
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = concentrationExpanded)
                                },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = concentrationExpanded,
                                onDismissRequest = { concentrationExpanded = false }
                            ) {
                                concentrationOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            viewModel.updateConcentration(option)
                                            concentrationExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Tech Stack
                        SetupTextField(
                            value = uiState.techStack,
                            onValueChange = viewModel::updateTechStack,
                            label = "Tech Stack (Optional)",
                            placeholder = "Kotlin, Python, React...",
                            icon = Icons.Default.Code,
                            iconColor = HmifBlue,
                            supportingText = "Comma-separated technologies"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(HmifTheme.spacing.xl))

                // Save button
                GradientButton(
                    text = "Complete Setup",
                    onClick = viewModel::saveProfile,
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// SETUP TEXT FIELD
// ════════════════════════════════════════════════════════════════

@Composable
private fun SetupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        },
        trailingIcon = trailingIcon,
        supportingText = supportingText?.let { { Text(it) } },
        readOnly = readOnly,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(HmifTheme.cornerRadius.md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HmifBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = HmifBlue
        ),
        modifier = modifier.fillMaxWidth()
    )
}
