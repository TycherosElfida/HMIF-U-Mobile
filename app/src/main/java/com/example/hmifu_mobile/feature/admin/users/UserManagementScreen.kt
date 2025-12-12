package com.example.hmifu_mobile.feature.admin.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.data.repository.UserProfile
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showRoleDialog by remember { mutableStateOf<UserProfile?>(null) }

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

    if (showRoleDialog != null) {
        RoleSelectionDialog(
            user = showRoleDialog!!,
            onDismiss = { showRoleDialog = null },
            onRoleSelected = { role ->
                viewModel.updateUserRole(showRoleDialog!!.uid, role)
                showRoleDialog = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HmifTheme.spacing.md),
                placeholder = { Text("Search by name...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                contentPadding = PaddingValues(HmifTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                items(uiState.users) { user ->
                    UserItem(
                        user = user,
                        onEditRole = { showRoleDialog = user }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: UserProfile,
    onEditRole: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEditRole
    ) {
        Row(
            modifier = Modifier.padding(HmifTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user.photoUrl != null) {
                // AsyncImage removed due to missing dependency
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.name.take(1))
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }
            
            Spacer(modifier = Modifier.width(HmifTheme.spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name.ifBlank { "No Name" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${user.nim} • ${user.role}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onEditRole) {
                Icon(Icons.Default.Edit, "Edit Role", tint = HmifBlue)
            }
        }
    }
}

@Composable
fun RoleSelectionDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf(
        "member" to "Member",
        "staff" to "Staff",
        "treasurer" to "Treasurer (Bendahara)",
        "secretary" to "Secretary (Sekretaris)",
        "vice_president" to "Vice President (Wakil Ketua)",
        "president" to "President (Ketua)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Role to ${user.name}") },
        text = {
            Column {
                roles.forEach { (roleId, roleName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = user.role == roleId,
                            onClick = { onRoleSelected(roleId) }
                        )
                        Text(
                            text = roleName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
