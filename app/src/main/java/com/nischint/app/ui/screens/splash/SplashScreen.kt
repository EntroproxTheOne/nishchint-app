package com.nischint.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nischint.app.R
import com.nischint.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Splash Screen shown during app initialization
 * Displays the app icon with animation
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        // Start animations
        startAnimation = true
        
        // Fade in and scale up animation
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        // Show splash for minimum duration
        delay(1500)
        
        // Fade out
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
        
        // Navigate to next screen
        onSplashComplete()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, SurfaceDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Nischint Logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name (optional, can be removed if not needed)
            // Text(
            //     text = "Nischint",
            //     style = MaterialTheme.typography.headlineMedium,
            //     color = YellowPrimary,
            //     fontWeight = FontWeight.Bold
            // )
        }
    }
}

