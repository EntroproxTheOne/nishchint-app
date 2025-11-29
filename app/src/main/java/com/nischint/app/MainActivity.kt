package com.nischint.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nischint.app.data.preferences.OnboardingPreferences
import com.nischint.app.navigation.NischintNavigation
import com.nischint.app.navigation.Screen
import com.nischint.app.ui.screens.splash.SplashScreen
import com.nischint.app.ui.theme.NischintTheme
import com.nischint.app.ui.utils.AppGradientBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NischintTheme {
                AppGradientBackground {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    var showSplash by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    if (showSplash) {
        SplashScreen(
            onSplashComplete = {
                showSplash = false
            }
        )
    } else {
        OnboardingCheck(startDestination) { destination ->
            startDestination = destination
        }
    }
}

@Composable
fun OnboardingCheck(
    currentDestination: String?,
    onDestinationReady: (String) -> Unit
) {
    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPreferences(context) }
    
    // Check onboarding status
    LaunchedEffect(Unit) {
        val isComplete = onboardingPrefs.getOnboardingComplete()
        val destination = if (isComplete) {
            Screen.Home.route  // User has completed onboarding
        } else {
            Screen.Onboarding.route  // First-time user
        }
        onDestinationReady(destination)
    }
    
    // Show navigation only after checking onboarding status
    currentDestination?.let { destination ->
        NischintNavigation(startDestination = destination)
    }
}
