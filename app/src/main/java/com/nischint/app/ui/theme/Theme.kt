package com.nischint.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Nischint Dark Theme Color Scheme
 * Dark gray background (#2D2D2D) with bright yellow (#FFD700) accents
 */
private val NischintDarkColorScheme = darkColorScheme(
    // Primary - Bright Yellow
    primary = YellowPrimary,              // #FFD700 - Main brand yellow
    onPrimary = TextOnYellow,             // Dark text on yellow
    primaryContainer = YellowDark,        // Darker yellow for containers
    onPrimaryContainer = TextWhite,       // White text on dark yellow
    
    // Secondary - Green (Safe Spending)
    secondary = GreenSafe,                // Safe/success green
    onSecondary = TextOnYellow,           // Dark text on green
    secondaryContainer = GreenSafe,
    onSecondaryContainer = TextWhite,
    
    // Tertiary - Optional accent
    tertiary = OrangeWarning,
    onTertiary = TextOnYellow,
    
    // Background & Surface
    background = BackgroundDark,          // #2D2D2D - Main dark gray
    onBackground = TextWhite,             // White text on dark background
    surface = SurfaceDark,                // #3A3A3A - Card surfaces
    onSurface = TextWhite,                // White text on surfaces
    surfaceVariant = SurfaceVariant,      // Slightly lighter surface
    onSurfaceVariant = TextGray,          // Gray text on variant surface
    
    // Outline & Borders
    outline = BorderSubtle,               // Subtle borders
    outlineVariant = BorderSubtle,
    
    // Error States
    error = RedDanger,                    // Error/danger red
    onError = TextWhite,                  // White text on red
    errorContainer = RedDanger,
    onErrorContainer = TextWhite,
    
    // Other
    inverseSurface = TextWhite,
    inverseOnSurface = BackgroundDark,
    inversePrimary = YellowDark,
    surfaceTint = YellowPrimary,
    scrim = ShadowColor
)

@Composable
fun NischintTheme(
    darkTheme: Boolean = true,  // Always use dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = NischintDarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dark status bar
            window.statusBarColor = BackgroundDark.toArgb()
            // Dark status bar icons (false = dark icons on light bg, but we want light icons on dark bg)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NischintTypography,
        content = content
    )
}
