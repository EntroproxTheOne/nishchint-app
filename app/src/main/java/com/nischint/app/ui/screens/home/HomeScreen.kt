package com.nischint.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nischint.app.data.api.MockData
import com.nischint.app.ui.components.*
import com.nischint.app.ui.theme.*

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
fun HomeScreenPreview() {
    NischintTheme {
        HomeScreen(
            onNavigateToGoals = {},
            onNavigateToTracker = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToGoals: () -> Unit,
    onNavigateToTracker: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddCashDialog by remember { mutableStateOf(false) }
    
    // Load data on first composition
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        HomeHeader(
            userName = uiState.dashboard?.name ?: "Loading...",
            onMenuClick = { },
            onProfileClick = { }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main Speedometer
        SpeedometerGauge(
            safeToSpend = uiState.dashboard?.safeToSpend ?: 0,
            maxAmount = 10000,
            riskLevel = uiState.dashboard?.riskLevel ?: "green",
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Risk Badge
        uiState.dashboard?.let { dashboard ->
            RiskBadge(level = dashboard.riskLevel)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Prediction Card
        uiState.dashboard?.prediction?.let { prediction ->
            PredictionCard(
                expenseLow = prediction.expenseLow,
                expenseHigh = prediction.expenseHigh,
                confidence = prediction.confidence,
                message = prediction.message,
                onClick = onNavigateToTracker
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Goal Progress Mini
        uiState.dashboard?.goal?.let { goal ->
            GoalProgressCard(
                goalName = goal.name,
                saved = goal.saved,
                target = goal.target,
                progress = goal.progress,
                streakDays = goal.streakDays,
                onClick = onNavigateToGoals
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Add Cash Button
        NeomorphicButton(
            onClick = { showAddCashDialog = true },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GoldPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Paisa Add Karo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
    }
    
    // Add Cash Dialog
    if (showAddCashDialog) {
        AddCashDialog(
            onDismiss = { showAddCashDialog = false },
            onAdd = { amount ->
                viewModel.addCash(amount)
                showAddCashDialog = false
            }
        )
    }
}

@Composable
fun HomeHeader(
    userName: String,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeomorphicIconButton(
            icon = Icons.Default.Menu,
            onClick = onMenuClick,
            size = 48.dp
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Namaste,",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        NeomorphicIconButton(
            icon = Icons.Default.Person,
            onClick = onProfileClick,
            size = 48.dp
        )
    }
}

@Composable
fun PredictionCard(
    expenseLow: Int,
    expenseHigh: Int,
    confidence: Float,
    message: String,
    onClick: () -> Unit
) {
    NeomorphicCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "📊 Agle Hafte",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹$expenseLow - ₹$expenseHigh",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            // Confidence badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = TealSafe.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = TealSafe,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GoalProgressCard(
    goalName: String,
    saved: Int,
    target: Int,
    progress: Float,
    streakDays: Int,
    onClick: () -> Unit
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🏍️ $goalName",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = GoldPrimary,
                    trackColor = ShadowDark.copy(alpha = 0.3f),
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₹$saved / ₹$target",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "🔥 $streakDays din streak!",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeWarning
                    )
                }
            }
        }
    }
}

@Composable
fun AddCashDialog(
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = "💰 Paisa Add Karo",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Aaj kitna income hua?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { char -> char.isDigit() } },
                    label = { Text("Amount (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100, 500, 1000).forEach { quickAmount ->
                        FilledTonalButton(
                            onClick = { amount = quickAmount.toString() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("₹$quickAmount")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    amount.toIntOrNull()?.let { onAdd(it) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
