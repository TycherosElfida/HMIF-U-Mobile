package com.example.hmifu_mobile.feature.finance

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
// import androidx.compose.material.icons.filled.AttachMoney
// import androidx.compose.material.icons.filled.MoneyOff
// import androidx.compose.material.icons.filled.TrendingDown
// import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.PrimaryButton
import com.example.hmifu_mobile.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FinanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(pageCount = { if (uiState.isTreasurer) 2 else 1 })
    val scope = rememberCoroutineScope()
    val titles = if (uiState.isTreasurer) listOf("Overview", "Add Transaction") else listOf("Overview")

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            // Switch back to overview on success
            scope.launch { pagerState.animateScrollToPage(0) }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Finance Dashboard") },
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
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = HmifBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = HmifBlue
                    )
                }
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OverviewPage(uiState)
                    1 -> if (uiState.isTreasurer) {
                        AddTransactionPage(
                            onSubmit = viewModel::addTransaction,
                            isLoading = uiState.isLoading
                        )
                    } else {
                        // Fallback empty
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewPage(uiState: FinanceUiState) {
    LazyColumn(
        contentPadding = PaddingValues(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg),
        modifier = Modifier.fillMaxSize()
    ) {
        // Balance Card
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = HmifTheme.cornerRadius.xl
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                ) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(uiState.balance),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.balance >= 0) HmifBlue else Error
                    )
                }
            }
        }

        // Summary Row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
                SummaryCard(
                    title = "Income",
                    amount = uiState.totalIncome,
                    icon = Icons.Default.Add, // Keeping Add/Delete icons for summary as they are better than Trending
                    color = Success,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Expense",
                    amount = uiState.totalExpense,
                    icon = Icons.Default.Delete, // Keeping Add/Delete icons for summary
                    color = Error,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Transactions Header
        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HmifTheme.spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(uiState.transactions) { transaction ->
                TransactionItem(transaction)
                Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val isIncome = transaction.type == TransactionType.INCOME
    val color = if (isIncome) Success else Error
    val icon = if (isIncome) Icons.Default.Add else Icons.Default.Delete // Keeping Add/Delete icons for transaction type as well

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color)
                }
                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDate(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = (if (isIncome) "+" else "-") + formatCurrency(transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun AddTransactionPage(
    onSubmit: (Double, TransactionType, String, String) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
    ) {
        // Type Selector
        Row(modifier = Modifier.fillMaxWidth()) {
            val isIncome = type == TransactionType.INCOME
            Button(
                onClick = { type = TransactionType.INCOME },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isIncome) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.Add, null) // Was TrendingUp
                Spacer(modifier = Modifier.width(8.dp))
                Text("Income")
            }
            Spacer(modifier = Modifier.width(HmifTheme.spacing.md))
            Button(
                onClick = { type = TransactionType.EXPENSE },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (!isIncome) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.Delete, null) // Was TrendingDown
                Spacer(modifier = Modifier.width(8.dp))
                Text("Expense")
            }
        }

        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
            label = { Text("Amount (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("Rp ") }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g. Event, Kas)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = if (isLoading) "Processing..." else "Save Transaction",
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (amountDouble != null && description.isNotBlank()) {
                    onSubmit(amountDouble, type, description, category.ifBlank { "Uncategorized" })
                }
            },
            enabled = !isLoading && amount.isNotBlank() && description.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
