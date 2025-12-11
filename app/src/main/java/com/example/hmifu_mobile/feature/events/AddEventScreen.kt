package com.example.hmifu_mobile.feature.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hmifu_mobile.ui.theme.GlassBorder
import com.example.hmifu_mobile.ui.theme.Primary
import com.example.hmifu_mobile.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Add Event Screen - Form to create new events (Admin only)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: AddEventViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEventAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id")) }

    // Handle success/error states
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onEventAdded()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // Date Picker Dialogs
    if (showStartDatePicker) {
        DatePickerDialog(
            viewModel = viewModel,
            isStartDate = true,
            initialDate = uiState.startDate,
            onDateSelected = { date ->
                viewModel.onStartDateChange(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            viewModel = viewModel,
            isStartDate = false,
            initialDate = uiState.endDate,
            onDateSelected = { date ->
                viewModel.onEndDateChange(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Event",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Title Field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Judul Event") },
                placeholder = { Text("Masukkan judul event") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null,
                        tint = Primary
                    )
                },
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = textFieldColors(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Deskripsi") },
                placeholder = { Text("Masukkan deskripsi event") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Primary
                    )
                },
                isError = uiState.descriptionError != null,
                supportingText = uiState.descriptionError?.let { { Text(it) } },
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = textFieldColors(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection Section
            Text(
                text = "Tanggal Event",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Start Date Picker Button
            DatePickerButton(
                label = if (uiState.isMultiDay) "Tanggal Mulai" else "Tanggal",
                selectedDate = uiState.startDate,
                dateFormat = dateFormat,
                isError = uiState.startDateError != null,
                errorMessage = uiState.startDateError,
                onClick = { showStartDatePicker = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-day Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onMultiDayChange(!uiState.isMultiDay) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.isMultiDay,
                    onCheckedChange = viewModel::onMultiDayChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Primary,
                        uncheckedColor = TextSecondary
                    )
                )
                Text(
                    text = "Event lebih dari 1 hari",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // End Date Picker (Animated visibility)
            AnimatedVisibility(
                visible = uiState.isMultiDay,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerButton(
                        label = "Tanggal Selesai",
                        selectedDate = uiState.endDate,
                        dateFormat = dateFormat,
                        isError = uiState.endDateError != null,
                        errorMessage = uiState.endDateError,
                        onClick = { showEndDatePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Field
            OutlinedTextField(
                value = uiState.location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Lokasi") },
                placeholder = { Text("Masukkan lokasi event") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Primary
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = textFieldColors(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image URL Field
            Text(
                text = "Gambar Event (Opsional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = viewModel::onImageUrlChange,
                label = { Text("URL Gambar Imgur") },
                placeholder = { Text("https://i.imgur.com/xxxxx.jpg") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Primary
                    )
                },
                isError = uiState.imageUrlError != null,
                supportingText = {
                    Text(uiState.imageUrlError ?: "Paste link gambar dari Imgur")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = textFieldColors(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Image Preview
            if (uiState.imageUrl.isNotBlank() && uiState.imageUrlError == null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = uiState.imageUrl,
                        contentDescription = "Preview gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = viewModel::addEvent,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.5f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Simpan Event",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DatePickerButton(
    label: String,
    selectedDate: Date?,
    dateFormat: SimpleDateFormat,
    isError: Boolean,
    errorMessage: String?,
    onClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.error else GlassBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(Color.White.copy(alpha = 0.02f))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Primary
                )
                Text(
                    text = selectedDate?.let { dateFormat.format(it) } ?: label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedDate != null) 
                        MaterialTheme.colorScheme.onBackground 
                    else 
                        TextSecondary
                )
            }
        }
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    viewModel: AddEventViewModel,
    isStartDate: Boolean,
    initialDate: Date?,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.time ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("OK", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    cursorColor = Primary,
    focusedBorderColor = Primary,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = Primary,
    unfocusedLabelColor = TextSecondary,
    focusedPlaceholderColor = TextSecondary,
    unfocusedPlaceholderColor = TextSecondary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error
)
