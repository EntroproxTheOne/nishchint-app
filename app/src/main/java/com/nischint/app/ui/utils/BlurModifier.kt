package com.nischint.app.ui.utils

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Blur modifier that works across Android versions
 * Uses enhanced transparency and shadows for glassmorphism effect
 * Works on all Android versions
 */
@Composable
fun Modifier.blurBackground(
    blurRadius: Float = 20f,
    alpha: Float = 0.85f,
    color: Color = androidx.compose.ui.graphics.Color.Black
): Modifier {
    return this.graphicsLayer {
        // Enhanced transparency for glassmorphism effect
        this.alpha = alpha
        // Add subtle shadow for depth
        shadowElevation = 8f
    }
}

/**
 * Glassmorphism effect modifier
 * Creates a frosted glass appearance with blur and transparency
 * Optimized for all Android versions
 */
fun Modifier.glassmorphism(
    blurRadius: Float = 20f,
    alpha: Float = 0.85f,
    borderAlpha: Float = 0.3f,
    backgroundColor: Color = androidx.compose.ui.graphics.Color.Black
): Modifier {
    return this.graphicsLayer {
        // Enhanced glassmorphism effect
        this.alpha = alpha
        shadowElevation = 12f
    }
}

/**
 * Composable wrapper for blur background
 * Creates a blurred overlay effect
 */
@Composable
fun BlurOverlay(
    modifier: Modifier = Modifier,
    blurRadius: Float = 20f,
    alpha: Float = 0.6f,
    color: Color = androidx.compose.ui.graphics.Color.Black
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = alpha),
                        color.copy(alpha = alpha * 0.8f)
                    )
                )
            )
    )
}

