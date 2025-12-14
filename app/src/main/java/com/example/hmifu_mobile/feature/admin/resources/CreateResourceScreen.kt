package com.example.hmifu_mobile.feature.admin.resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.FilterChip
import com.example.hmifu_mobile.ui.components.PrimaryButton
import com.example.hmifu_mobile.ui.theme.HmifTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResourceScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateResourceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.success) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
        if (uiState.success) {
            snackbarHostState.showSnackbar("Resource added successfully")
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Resource") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = HmifTheme.spacing.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
        ) {
            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title (e.g. UAS 2023)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Subject
            OutlinedTextField(
                value = uiState.subject,
                onValueChange = viewModel::onSubjectChange,
                label = { Text("Subject (e.g. Algoritma)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Link / URL
            OutlinedTextField(
                value = uiState.fileUrl,
                onValueChange = viewModel::onFileUrlChange,
                label = { Text("File URL (Google Drive, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Year
            OutlinedTextField(
                value = uiState.year.toString(),
                onValueChange = viewModel::onYearChange,
                label = { Text("Year") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Semester Selection
            Text(
                text = "Semester",
                style = MaterialTheme.typography.titleSmall
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                val semesters = listOf(1, 2, 3, 4, 5, 6, 7, 8)
                // Simplified selection for MVP - maybe dropdown later or just horizontal scroll
                // Using just 2 rows of chips manually/roughly or a flow row if available.
                // For now, simple text field for simplicity or a horizontal scroll row
            }
            // Better: Simple int input or a few choices. Let's stick to Slider or just input.
            // Wait, I implemented FilterChip in FilterRow.
            // Let's us a simple Row of common semesters or just an input.
            // Actually, a simple Number input is safest for now to save space, or a dropdown.
            // Let's use a standard OutlinedTextField for Semester for simplicity in this iteration.
             OutlinedTextField(
                value = uiState.semester.toString(),
                onValueChange = { 
                    if (it.all { c -> c.isDigit() }) {
                        val v = it.toIntOrNull()
                        if (v != null && v in 1..8) viewModel.onSemesterChange(v)
                        else if (it.isEmpty()) {} // Allow empty while typing? 
                        // ViewModel expects Int. Let's just handle it safe.
                    }
                },
                label = { Text("Semester (1-8)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )


            // Type Selection
            Text(
                text = "Type",
                style = MaterialTheme.typography.titleSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("EXAM", "QUIZ", "ASSIGNMENT").forEach { type ->
                    FilterChip(
                        text = type,
                        selected = uiState.type == type,
                        onClick = { viewModel.onTypeChange(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.xl))

            PrimaryButton(
                text = if (uiState.isLoading) "Saving..." else "Create Resource",
                onClick = { viewModel.createResource() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HmifTheme.spacing.xl))
        }
    }
}
