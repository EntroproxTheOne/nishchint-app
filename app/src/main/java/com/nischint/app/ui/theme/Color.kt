package com.nischint.app.ui.theme

import androidx.compose.ui.graphics.Color

// ============ DARK THEME COLORS ============

// Primary - Bright Yellow (Main Brand Color)
val YellowPrimary = Color(0xFFFFD700)  // Bright yellow from web app
val YellowLight = Color(0xFFFFE44D)    // Lighter yellow for highlights
val YellowDark = Color(0xFFE6C200)     // Darker yellow for pressed states

// Dark Backgrounds
val BackgroundDark = Color(0xFF2D2D2D)  // Main dark gray background
val SurfaceDark = Color(0xFF3A3A3A)     // Card/surface dark gray
val SurfaceVariant = Color(0xFF424242) // Slightly lighter for emphasis

// Text Colors - Dark Theme
val TextWhite = Color(0xFFFFFFFF)       // Primary text on dark
val TextGray = Color(0xFFB0B0B0)        // Secondary text
val TextDarkGray = Color(0xFF808080)    // Tertiary/disabled text
val TextOnYellow = Color(0xFF1A1A1A)    // Text on yellow backgrounds

// Accent Colors
val GreenSafe = Color(0xFF00C853)       // Safe spending indicator
val RedDanger = Color(0xFFFF3B30)       // Danger/overspending
val OrangeWarning = Color(0xFFFF9500)   // Warning/caution

// Status Colors
val SuccessGreen = Color(0xFF34C759)
val ErrorRed = Color(0xFFFF3B30)
val InfoBlue = Color(0xFF007AFF)

// Shadows & Borders
val BorderSubtle = Color(0xFF4D4D4D)    // Subtle borders for dark theme
val ShadowColor = Color(0xFF000000)     // Shadows (use with alpha)
val GlowYellow = Color(0xFFFFD700)      // For glow effects (use with alpha)

// ============ LEGACY COLORS (kept for compatibility) ============

// Old colors - keeping for gradual migration
val GoldPrimary = YellowPrimary         // Alias to new yellow
val GoldLight = YellowLight
val GoldDark = YellowDark
val TealSafe = GreenSafe
val TealDark = Color(0xFF00A040)
val Background = BackgroundDark          // Alias to dark background
val Surface = SurfaceDark                // Alias to dark surface
val ShadowDark = Color(0xFF1A1A1A)
val ShadowLight = Color(0xFF4D4D4D)
val TextPrimary = TextWhite              // Alias to white text
val TextSecondary = TextGray             // Alias to gray text
val TextOnGold = TextOnYellow

// ============ UTILITY FUNCTIONS ============

// Risk Level Colors
fun getRiskColor(level: String): Color = when (level.lowercase()) {
    "green" -> GreenSafe
    "yellow" -> OrangeWarning
    "red" -> RedDanger
    else -> GreenSafe
}
