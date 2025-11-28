package com.nischint.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nischint.app.ui.theme.*
import com.nischint.app.ui.screens.home.HomeScreen
import com.nischint.app.ui.screens.tracker.TrackerScreen
import com.nischint.app.ui.screens.goals.GoalsScreen
import com.nischint.app.ui.screens.onboarding.OnboardingScreen

// Navigation destinations
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Tracker : Screen("tracker", "Tracker", Icons.Filled.Receipt, Icons.Outlined.Receipt)
    object Goals : Screen("goals", "Goals", Icons.Filled.Flag, Icons.Outlined.Flag)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Home, Icons.Outlined.Home)
}

val bottomNavItems = listOf(Screen.Home, Screen.Tracker, Screen.Goals)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NischintNavigation(
    startDestination: String = Screen.Home.route  // Change to Onboarding for first-time users
) {
    val navController = rememberNavController()
    var showMicDialog by remember { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom nav on onboarding screen
    val showBottomBar = currentRoute != Screen.Onboarding.route
    
    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NischintBottomBar(
                    navController = navController,
                    onMicClick = { showMicDialog = true }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToGoals = { navController.navigate(Screen.Goals.route) },
                    onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
                )
            }
            composable(Screen.Tracker.route) {
                TrackerScreen()
            }
            composable(Screen.Goals.route) {
                GoalsScreen()
            }
        }
    }
    
    // Voice input dialog
    if (showMicDialog) {
        VoiceInputDialog(
            onDismiss = { showMicDialog = false },
            onCommand = { command ->
                showMicDialog = false
                handleVoiceCommand(command, navController)
            }
        )
    }
}

@Composable
fun NischintBottomBar(
    navController: NavController,
    onMicClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Bottom nav container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEachIndexed { index, screen ->
                    // Add spacer in middle for mic button
                    if (index == 1) {
                        Spacer(modifier = Modifier.width(72.dp))
                    }
                    
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title,
                                tint = if (selected) GoldPrimary else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (selected) GoldPrimary else TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = GoldLight.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
        
        // Floating Mic Button
        FloatingActionButton(
            onClick = onMicClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(64.dp),
            shape = CircleShape,
            containerColor = GoldPrimary,
            contentColor = TextOnGold
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Input",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onCommand: (String) -> Unit
) {
    var isListening by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = if (isListening) "🎤 Bol raha hoon..." else "Kya bolna hai?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Bol: 'Bike', 'Tracker', 'Income'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Demo buttons for testing without actual voice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(onClick = { onCommand("bike") }) {
                        Text("🏍️ Bike")
                    }
                    FilledTonalButton(onClick = { onCommand("tracker") }) {
                        Text("📊 Tracker")
                    }
                    FilledTonalButton(onClick = { onCommand("income") }) {
                        Text("💰 Income")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GoldPrimary)
            }
        }
    )
}

fun handleVoiceCommand(command: String, navController: NavController) {
    val lower = command.lowercase()
    when {
        lower.contains("bike") || lower.contains("goal") || lower.contains("saving") -> {
            navController.navigate(Screen.Goals.route)
        }
        lower.contains("tracker") || lower.contains("kharcha") || lower.contains("expense") -> {
            navController.navigate(Screen.Tracker.route)
        }
        lower.contains("home") || lower.contains("ghar") -> {
            navController.navigate(Screen.Home.route)
        }
        lower.contains("income") || lower.contains("cash") || lower.contains("paisa") -> {
            // TODO: Show add cash dialog
            navController.navigate(Screen.Home.route)
        }
    }
}
