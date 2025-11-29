package com.nischint.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.nischint.app.data.api.MockData
import com.nischint.app.ui.components.*
import com.nischint.app.ui.screens.home.StorageAnalysisCard
import com.nischint.app.ui.theme.*
import com.nischint.app.ui.utils.glassmorphism
import com.nischint.app.ui.utils.AnimatedCardAppearance
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch

@Preview(showBackground = true, backgroundColor = 0xFF2D2D2D)
@Composable
fun HomeScreenPreview() {
    NischintTheme {
        HomeScreen(
            onNavigateToGoals = {},
            onNavigateToTracker = {},
            onNavigateToProfile = {},
            onMicClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel? = null,
    onNavigateToGoals: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onMicClick: () -> Unit = {}
) {
    // Get Application context first
    val application = LocalContext.current.applicationContext as Application
    
    // Create ViewModel with Application context inside composable body
    val actualViewModel: HomeViewModel = viewModel ?: viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(application) as T
            }
        }
    )
    
    val uiState by actualViewModel.uiState.collectAsState()
    var showAddCashDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val view = LocalView.current  // Get view outside lambda
    
    // Load data on first composition
    LaunchedEffect(Unit) {
        actualViewModel.loadDashboard()
    }
    
    // Navigation drawer - properly configured to not overlap content
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            NavigationDrawerContent(
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onNavigateToHome = {
                    scope.launch {
                        drawerState.close()
                        // Home is already the current screen, just close drawer
                    }
                },
                onNavigateToGoals = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToGoals()
                    }
                },
                onNavigateToTracker = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToTracker()
                    }
                },
                onNavigateToSettings = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToSettings()
                    }
                }
            )
        },
        scrimColor = ShadowColor.copy(alpha = 0.7f),  // Darker scrim with blur effect
        modifier = Modifier.graphicsLayer {
            // Blur effect for drawer overlay
            alpha = 1f
        }
    ) {
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
                onMenuClick = { scope.launch { drawerState.open() } },  // Open drawer on menu click
                onProfileClick = onNavigateToProfile  // Navigate to Profile screen
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Mic Button - Centered below speedometer (where red arrow points)
            FloatingActionButton(
                onClick = {
                    // Haptic feedback for mic button
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                    onMicClick()
                },
                modifier = Modifier.size(72.dp),  // Large yellow circle
                shape = CircleShape,
                containerColor = YellowPrimary,
                contentColor = TextOnYellow,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    modifier = Modifier.size(32.dp),
                    tint = TextOnYellow
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Prediction Card with animation
            uiState.dashboard?.prediction?.let { prediction ->
                AnimatedCardAppearance {
                    PredictionCard(
                        expenseLow = prediction.expenseLow,
                        expenseHigh = prediction.expenseHigh,
                        confidence = prediction.confidence,
                        message = prediction.message,
                        onClick = onNavigateToTracker
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Goal Progress Mini with animation
            uiState.dashboard?.goal?.let { goal ->
                AnimatedCardAppearance {
                    GoalProgressCard(
                        goalName = goal.name,
                        saved = goal.saved,
                        target = goal.target,
                        progress = goal.progress,
                        streakDays = goal.streakDays,
                        onClick = onNavigateToGoals
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Storage Analysis Card (Gemini-powered insights) with animation
            AnimatedCardAppearance {
                StorageAnalysisCard(
                    transactions = emptyList(),  // Empty list - will load from database automatically
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Add Cash Button
            NeomorphicButton(
                onClick = { showAddCashDialog = true },
                modifier = Modifier.fillMaxWidth()
                // Using default yellow background
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
                    actualViewModel.addCash(amount)
                    showAddCashDialog = false
                }
            )
        }
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
                color = TextGray  // Updated to TextGray
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextWhite  // Updated to TextWhite
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
                    color = TextWhite,  // Updated to TextWhite
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹$expenseLow - ₹$expenseHigh",
                    style = MaterialTheme.typography.headlineSmall,
                    color = YellowPrimary,  // Yellow for money
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray  // Updated to TextGray
                )
            }
            
            // Confidence badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = GreenSafe.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = GreenSafe,
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
                    color = TextWhite,  // Updated to TextWhite
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = YellowPrimary,
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
                        color = TextGray  // Updated to TextGray
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
        containerColor = SurfaceDark.copy(alpha = 0.95f),
        modifier = Modifier.graphicsLayer {
            // Blur effect for dialog
            alpha = 0.95f
            shadowElevation = 16f
        },
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
                    color = TextGray  // Updated to TextGray
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
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)  // Updated to TextGray
            }
        }
    )
}

/**
 * Navigation drawer content
 */
@Composable
fun NavigationDrawerContent(
    onCloseDrawer: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGoals: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(280.dp)  // Standard Material Design drawer width
            .fillMaxHeight()
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nischint",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextWhite
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Menu Items
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = YellowPrimary) },
            label = { Text("Home", color = TextWhite) },
            selected = false,
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Flag, contentDescription = null, tint = YellowPrimary) },
            label = { Text("Goals", color = TextWhite) },
            selected = false,
            onClick = onNavigateToGoals,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = YellowPrimary) },
            label = { Text("Tracker", color = TextWhite) },
            selected = false,
            onClick = onNavigateToTracker,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = YellowPrimary) },
            label = { Text("Settings", color = TextWhite) },
            selected = false,
            onClick = onNavigateToSettings,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = YellowPrimary) },
            label = { Text("About", color = TextWhite) },
            selected = false,
            onClick = { /* TODO: Show about dialog */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
