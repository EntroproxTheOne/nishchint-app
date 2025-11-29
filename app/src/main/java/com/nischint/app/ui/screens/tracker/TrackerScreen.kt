package com.nischint.app.ui.screens.tracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.nischint.app.data.api.MockData
import com.nischint.app.data.models.Transaction
import com.nischint.app.ui.components.*
import com.nischint.app.ui.theme.*
import com.nischint.app.utils.rememberSmsPermission
import com.nischint.app.utils.SmsPermissionRationaleDialog
import com.nischint.app.utils.SmsPermissionDeniedDialog
import com.nischint.app.utils.openAppSettings
import com.nischint.app.utils.rememberImagePicker
import kotlinx.coroutines.delay

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
fun TrackerScreenPreview() {
    NischintTheme {
        TrackerScreen()
    }
}

@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel? = null
) {
    // Get Application context first
    val application = LocalContext.current.applicationContext as android.app.Application
    
    // Create ViewModel with Application context inside composable body
    val actualViewModel: TrackerViewModel = viewModel ?: viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TrackerViewModel(application) as T
            }
        }
    )
    
    val context = LocalContext.current
    val uiState by actualViewModel.uiState.collectAsState()
    
    // SMS Permission handling
    var showSmsPermissionRationale by remember { mutableStateOf(false) }
    var showSmsPermissionDenied by remember { mutableStateOf(false) }
    val (smsPermissionState, requestSmsPermission) = rememberSmsPermission()
    
    // Screenshot picker
    val pickImage = rememberImagePicker { uri ->
        uri?.let { actualViewModel.parseScreenshot(it) }
    }
    
    // Handle SMS sync with permission check
    val handleSmsSync: () -> Unit = {
        when (smsPermissionState) {
            is com.nischint.app.utils.SmsPermissionState.Granted -> {
                actualViewModel.parseSmsTransactions()
            }
            is com.nischint.app.utils.SmsPermissionState.NotRequested,
            is com.nischint.app.utils.SmsPermissionState.Denied -> {
                showSmsPermissionRationale = true
            }
            is com.nischint.app.utils.SmsPermissionState.PermanentlyDenied -> {
                showSmsPermissionDenied = true
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "📊 Tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite  // Updated to TextWhite
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category Toggle
        CategoryToggle(
            selectedBusiness = uiState.showBusiness,
            onToggle = { actualViewModel.toggleCategory() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // SMS Sync Button
        NeomorphicButton(
            onClick = handleSmsSync,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GreenSafe  // Green for safe/sync action
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.isSyncing) "Parsing SMS..." else "SMS Parse Karo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Screenshot Parser Button
        NeomorphicButton(
            onClick = { pickImage() },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = YellowPrimary
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.isParsingScreenshot) "Parsing Screenshot..." else "Screenshot Se Transaction Add Karo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Card
        if (uiState.transactions.isNotEmpty()) {
            TransactionSummaryCard(
                totalExpense = uiState.totalExpense,
                totalIncome = uiState.totalIncome
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Error message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: $error",
                color = RedDanger,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Transaction List
        if (uiState.transactions.isEmpty() && !uiState.isSyncing && !uiState.isParsingScreenshot) {
            EmptyTransactionState()
        } else {
            if (uiState.isSyncing || uiState.isParsingScreenshot) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YellowPrimary)
                }
            } else {
                TransactionList(
                    transactions = uiState.filteredTransactions,
                    isSyncing = uiState.isSyncing
                )
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
    }
    
    // SMS Permission Rationale Dialog
    if (showSmsPermissionRationale) {
        SmsPermissionRationaleDialog(
            onDismiss = { showSmsPermissionRationale = false },
            onConfirm = {
                showSmsPermissionRationale = false
                requestSmsPermission()
            }
        )
    }
    
    // SMS Permission Denied Dialog
    if (showSmsPermissionDenied) {
        SmsPermissionDeniedDialog(
            onDismiss = { showSmsPermissionDenied = false },
            onOpenSettings = {
                context.openAppSettings()
                showSmsPermissionDenied = false
            }
        )
    }
}

@Composable
fun CategoryToggle(
    selectedBusiness: Boolean,
    onToggle: () -> Unit
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToggleOption(
                text = "🏪 Dhanda",
                selected = selectedBusiness,
                onClick = { if (!selectedBusiness) onToggle() },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ToggleOption(
                text = "🏠 Ghar",
                selected = !selectedBusiness,
                onClick = { if (selectedBusiness) onToggle() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ToggleOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) GoldPrimary else Surface,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) TextOnGold else TextSecondary,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun TransactionSummaryCard(
    totalExpense: Int,
    totalIncome: Int
) {
    NeomorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Kharcha",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray  // Updated to TextGray
                )
                Text(
                    text = "-₹$totalExpense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RedDanger
                )
            }
            
            Divider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = ShadowDark.copy(alpha = 0.3f)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Income",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray  // Updated to TextGray
                )
                Text(
                    text = "+₹$totalIncome",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TealSafe
                )
            }
        }
    }
}

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    isSyncing: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isSyncing) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            }
        }
        
        itemsIndexed(transactions) { index, transaction ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                TransactionItemCard(transaction = transaction)
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: Transaction
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category emoji
                Text(
                    text = transaction.categoryEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = transaction.merchant,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite  // Updated to TextWhite
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category tag
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldLight.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = transaction.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        // Business tag
                        if (transaction.isBusiness) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TealSafe.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Dhanda",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TealDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Amount
            MoneyText(
                amount = transaction.amount,
                showSign = true,
                isExpense = transaction.isExpense,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun EmptyTransactionState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📱",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Koi transaction nahi hai",
                style = MaterialTheme.typography.titleMedium,
                color = TextGray  // Updated to TextGray
            )
            Text(
                text = "SMS Sync karo upar se",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray  // Updated to TextGray
            )
        }
    }
}
