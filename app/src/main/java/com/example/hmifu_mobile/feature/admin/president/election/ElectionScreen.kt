package com.example.hmifu_mobile.feature.admin.president.election

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.CandidateEntity
import com.example.hmifu_mobile.ui.components.HmifCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: ElectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Dialog states
    var showAddCandidateDialog by remember { mutableStateOf(false) }
    var selectedCandidate by remember { mutableStateOf<CandidateEntity?>(null) } // For details/voting logic

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HMIF Election 2025") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // Only show if Admin/President mode 
            // Ideally we check role, but for now allow anyone to access adding for demo 
            // or maybe restricted by VM State?
            // Let's assume this screen is primarily for President management, 
            // but the voting function is for everyone.
            // For now, always show FAB to add candidates for the "President" user case.
            FloatingActionButton(onClick = { showAddCandidateDialog = true }) {
                Icon(Icons.Default.Add, "Add Candidate")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Status Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (uiState.hasVoted) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = if (uiState.hasVoted) "You have voted!" else "Voting is Open",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (uiState.hasVoted) "Thank you for participating." else "Select a candidate to view details and vote.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                if (uiState.candidates.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No candidates yet.")
                    }
                } else {
                    LazyColumn(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val totalVotes = uiState.candidates.sumOf { it.voteCount }.coerceAtLeast(1)

                        items(uiState.candidates) { candidate ->
                            CandidateCard(
                                candidate = candidate,
                                hasVoted = uiState.hasVoted,
                                totalVotes = totalVotes,
                                onClick = { selectedCandidate = candidate }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddCandidateDialog) {
        AddCandidateDialog(
            onDismiss = { showAddCandidateDialog = false },
            onConfirm = { name, number, vision, mission ->
                viewModel.addCandidate(name, number, vision, mission)
                showAddCandidateDialog = false
            }
        )
    }

    if (selectedCandidate != null) {
        CandidateDetailDialog(
            candidate = selectedCandidate!!,
            onDismiss = { selectedCandidate = null },
            onVote = {
                viewModel.castVote(selectedCandidate!!.id)
                selectedCandidate = null
            },
            canVote = !uiState.hasVoted
        )
    }
}

@Composable
fun CandidateCard(
    candidate: CandidateEntity,
    hasVoted: Boolean,
    totalVotes: Int,
    onClick: () -> Unit
) {
    HmifCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (candidate.photoUrl != null) {
                    // Load image
                } else {
                    Text(
                        text = "#${candidate.number}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = candidate.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (hasVoted) {
                    val percentage = (candidate.voteCount.toFloat() / totalVotes.toFloat())
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    Text(
                        text = "${(percentage * 100).toInt()}% (${candidate.voteCount} votes)",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    Text(
                        text = "Click for details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddCandidateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var vision by remember { mutableStateOf("") }
    var mission by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Candidate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = number,
                    onValueChange = { if (it.all { c -> c.isDigit() }) number = it },
                    label = { Text("Number (Urut)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = vision, onValueChange = { vision = it }, label = { Text("Vision") }, minLines = 2)
                OutlinedTextField(value = mission, onValueChange = { mission = it }, label = { Text("Mission") }, minLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && number.isNotBlank()) {
                        onConfirm(name, number.toInt(), vision, mission)
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CandidateDetailDialog(
    candidate: CandidateEntity,
    onDismiss: () -> Unit,
    onVote: () -> Unit,
    canVote: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                 Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("#${candidate.number}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(candidate.name, style = MaterialTheme.typography.titleMedium)
                    Text("Candidate Details", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Vision", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(candidate.vision, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Mission", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(candidate.mission, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            if (canVote) {
                Button(onClick = onVote, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VOTE FOR THIS CANDIDATE")
                }
            } else {
                 TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
        dismissButton = {
            if (canVote) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
