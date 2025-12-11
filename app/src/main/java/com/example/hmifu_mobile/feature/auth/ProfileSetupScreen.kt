package com.example.hmifu_mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Profile setup screen for new users.
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

    // Navigate on completion
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onProfileComplete()
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tell us about yourself",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                placeholder = { Text("John Doe") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NIM
            OutlinedTextField(
                value = uiState.nim,
                onValueChange = viewModel::updateNim,
                label = { Text("NIM (Student ID)") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                placeholder = { Text("2024001234") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Angkatan dropdown
            ExposedDropdownMenuBox(
                expanded = angkatanExpanded,
                onExpandedChange = { angkatanExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.angkatan,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Batch Year (Angkatan)") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = angkatanExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Concentration dropdown
            ExposedDropdownMenuBox(
                expanded = concentrationExpanded,
                onExpandedChange = { concentrationExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.concentration,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Concentration") },
                    leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = concentrationExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Tech Stack (optional)
            OutlinedTextField(
                value = uiState.techStack,
                onValueChange = viewModel::updateTechStack,
                label = { Text("Tech Stack (Optional)") },
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                placeholder = { Text("Kotlin, Python, React...") },
                supportingText = { Text("Comma-separated list of technologies") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = viewModel::saveProfile,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Setup")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

