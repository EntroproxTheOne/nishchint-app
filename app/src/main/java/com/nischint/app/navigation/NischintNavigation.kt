package com.nischint.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.nischint.app.ui.screens.profile.ProfileScreen
import com.nischint.app.ui.screens.settings.SettingsScreen
import com.nischint.app.utils.rememberVoiceRecognitionManager
import com.nischint.app.utils.rememberAudioPermission
import com.nischint.app.utils.AudioPermissionRationaleDialog
import com.nischint.app.utils.AudioPermissionDeniedDialog
import com.nischint.app.utils.openAppSettings
import com.nischint.app.ui.utils.glassmorphism
import com.nischint.app.ui.utils.AppGradientBackground
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.graphicsLayer

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
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Tracker, Screen.Goals)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NischintNavigation(
    startDestination: String = Screen.Home.route  // Change to Onboarding for first-time users
) {
    val navController = rememberNavController()
    var showMicDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var showPermissionDenied by remember { mutableStateOf(false) }
    val context = LocalContext.current  // Get context outside lambda
    
    // Audio permission handling
    val (permissionState, requestPermission) = com.nischint.app.utils.rememberAudioPermission()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom nav on onboarding screen
    val showBottomBar = currentRoute != Screen.Onboarding.route
    
    // Handle mic button click with permission check
    val handleMicClick: () -> Unit = {
        when (permissionState) {
            is com.nischint.app.utils.AudioPermissionState.Granted -> {
                showMicDialog = true
            }
            is com.nischint.app.utils.AudioPermissionState.NotRequested,
            is com.nischint.app.utils.AudioPermissionState.Denied -> {
                showPermissionRationale = true
            }
            is com.nischint.app.utils.AudioPermissionState.PermanentlyDenied -> {
                showPermissionDenied = true
            }
        }
    }
    
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,  // Transparent to show gradient
        bottomBar = {
            if (showBottomBar) {
                NischintBottomBar(
                    navController = navController
                    // Mic button removed - now in center of HomeScreen
                )
            }
        }
    ) { innerPadding ->
        AppGradientBackground {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + slideInHorizontally(
                    initialOffsetX = { it / 3 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + slideOutHorizontally(
                    targetOffsetX = { it / 3 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
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
                    onNavigateToGoals = { 
                        navController.navigate(Screen.Goals.route) {
                            // Keep Home in back stack for navigation back
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTracker = { 
                        navController.navigate(Screen.Tracker.route) {
                            // Keep Home in back stack for navigation back
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProfile = { 
                        navController.navigate(Screen.Profile.route) {
                            // Keep Home in back stack for navigation back
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route) {
                            // Keep Home in back stack for navigation back
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMicClick = handleMicClick
                )
            }
            composable(Screen.Tracker.route) {
                TrackerScreen()
            }
            composable(Screen.Goals.route) {
                GoalsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
        }
    }
    
    // Permission rationale dialog
    if (showPermissionRationale) {
        com.nischint.app.utils.AudioPermissionRationaleDialog(
            onDismiss = { showPermissionRationale = false },
            onConfirm = {
                showPermissionRationale = false
                requestPermission()
            }
        )
    }
    
    // Permission denied dialog
    if (showPermissionDenied) {
        com.nischint.app.utils.AudioPermissionDeniedDialog(
            onDismiss = { showPermissionDenied = false },
            onOpenSettings = {
                context.openAppSettings()
                showPermissionDenied = false
            }
        )
    }
    
    // Voice input dialog (only show if permission granted)
    if (showMicDialog && permissionState is com.nischint.app.utils.AudioPermissionState.Granted) {
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
    navController: NavController
    // Mic button removed - now positioned in center of HomeScreen
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val view = LocalView.current  // Get view outside lambda
    
    // Bottom nav container with frosted glass effect - auto-resizable width
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()  // Auto-resize based on content
                .height(64.dp)  // Reduced height for better fit
                .align(Alignment.Center)  // Center the nav bar
                .glassmorphism(
                    blurRadius = 20f,
                    alpha = 0.85f,
                    borderAlpha = 0.3f,
                    backgroundColor = SurfaceDark
                )
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = ShadowColor.copy(alpha = 0.4f),
                    spotColor = ShadowColor.copy(alpha = 0.4f)
                )
                .graphicsLayer {
                    // Enhanced blur effect
                    alpha = 0.9f
                    shadowElevation = 12f
                },
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark.copy(alpha = 0.85f),  // Semi-transparent for glass effect
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = BorderSubtle.copy(alpha = 0.3f)  // Subtle border for definition
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),  // Internal padding for spacing
                horizontalArrangement = Arrangement.spacedBy(4.dp),  // Fixed spacing between items
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            // Haptic feedback for navigation
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                            
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
                                tint = if (selected) YellowPrimary else TextGray,
                                modifier = Modifier.size(24.dp)  // Fixed icon size
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (selected) YellowPrimary else TextGray,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp  // Fixed font size
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = YellowPrimary.copy(alpha = 0.15f),
                            selectedIconColor = YellowPrimary,
                            unselectedIconColor = TextGray,
                            selectedTextColor = YellowPrimary,
                            unselectedTextColor = TextGray
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)  // Item padding to prevent overlap
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onCommand: (String) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val voiceManager = rememberVoiceRecognitionManager()
    val sttService = remember { com.nischint.app.data.api.GeminiSTTService() }
    
    val voiceState by voiceManager.state.collectAsState()
    var transcript by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    
    // Start listening when dialog opens with haptic feedback
    LaunchedEffect(Unit) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
        voiceManager.startListening()
    }
    
    // Handle voice recognition state
    LaunchedEffect(voiceState) {
        when (voiceState) {
            is com.nischint.app.utils.VoiceRecognitionState.Result -> {
                val result = voiceState as com.nischint.app.utils.VoiceRecognitionState.Result
                transcript = result.transcript
                
                if (result.isFinal) {
                    isProcessing = true
                    // Haptic feedback for successful recognition
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                    // Enhance transcript with Gemini STT
                    // We're already in a coroutine context (LaunchedEffect), so we can directly collect
                    sttService.enhanceTranscript(transcript).collect { enhanced ->
                        isProcessing = false
                        onCommand(enhanced)
                        onDismiss()
                    }
                }
            }
            is com.nischint.app.utils.VoiceRecognitionState.Error -> {
                val error = voiceState as com.nischint.app.utils.VoiceRecognitionState.Error
                transcript = "Error: ${error.message}"
            }
            else -> {}
        }
    }
    
    AlertDialog(
        onDismissRequest = {
            voiceManager.cancel()
            onDismiss()
        },
        containerColor = SurfaceDark.copy(alpha = 0.95f),
        modifier = Modifier.graphicsLayer {
            // Blur effect for dialog
            alpha = 0.95f
            shadowElevation = 16f
        },
        title = {
            Text(
                text = when (voiceState) {
                    is com.nischint.app.utils.VoiceRecognitionState.Listening -> "🎤 Sun raha hoon..."
                    is com.nischint.app.utils.VoiceRecognitionState.Result -> "✅ Samajh gaya!"
                    is com.nischint.app.utils.VoiceRecognitionState.Error -> "❌ Error"
                    else -> "🎤 Bol raha hoon..."
                },
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (transcript.isNotEmpty()) {
                    Text(
                        text = transcript,
                        style = MaterialTheme.typography.bodyLarge,
                        color = YellowPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "Bol: 'Bike', 'Tracker', 'Income'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
                
                if (isProcessing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = YellowPrimary
                    )
                    Text(
                        text = "Gemini se enhance kar raha hoon...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                voiceManager.cancel()
                onDismiss()
            }) {
                Text("Cancel", color = YellowPrimary)
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
