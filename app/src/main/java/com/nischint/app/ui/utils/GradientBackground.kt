package com.nischint.app.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.nischint.app.ui.theme.BackgroundDark
import com.nischint.app.ui.theme.SurfaceDark
import com.nischint.app.ui.theme.YellowPrimary

/**
 * Gradient background colors for the app
 */
object GradientColors {
    val DarkGradientStart = BackgroundDark
    val DarkGradientMiddle = Color(0xFF252525)  // Slightly lighter dark gray
    val DarkGradientEnd = SurfaceDark.copy(alpha = 0.8f)
    
    val YellowAccent = YellowPrimary.copy(alpha = 0.1f)  // Subtle yellow tint
}

/**
 * Main app gradient background
 * Creates a subtle dark gradient with yellow accent
 */
@Composable
fun AppGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientColors.DarkGradientStart,
                        GradientColors.DarkGradientMiddle,
                        GradientColors.DarkGradientEnd
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        content()
    }
}

/**
 * Subtle gradient background with yellow accent
 */
@Composable
fun SubtleGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GradientColors.YellowAccent,
                        GradientColors.DarkGradientStart
                    ),
                    center = Offset(0.5f, 0.3f),
                    radius = 1000f
                )
            )
    ) {
        content()
    }
}

/**
 * Gradient modifier for backgrounds
 */
fun Modifier.appGradientBackground(): Modifier {
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                GradientColors.DarkGradientStart,
                GradientColors.DarkGradientMiddle,
                GradientColors.DarkGradientEnd
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        )
    )
}

