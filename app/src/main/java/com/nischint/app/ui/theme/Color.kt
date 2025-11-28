package com.nischint.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary - Warm Gold (Prosperity)
val GoldPrimary = Color(0xFFF5A623)
val GoldLight = Color(0xFFFFD93D)
val GoldDark = Color(0xFFE09400)

// Safe Zone - Teal
val TealSafe = Color(0xFF00B894)
val TealDark = Color(0xFF00A884)

// Warning/Danger
val OrangeWarning = Color(0xFFFF6B35)
val RedDanger = Color(0xFFE74C3C)

// Neomorphism Base
val Background = Color(0xFFE8E8E8)
val Surface = Color(0xFFF0F0F0)
val ShadowDark = Color(0xFFBEBEBE)
val ShadowLight = Color(0xFFFFFFFF)

// Text
val TextPrimary = Color(0xFF2D3436)
val TextSecondary = Color(0xFF636E72)
val TextOnGold = Color(0xFFFFFFFF)

// Risk Level Colors
fun getRiskColor(level: String): Color = when (level.lowercase()) {
    "green" -> TealSafe
    "yellow" -> OrangeWarning
    "red" -> RedDanger
    else -> TealSafe
}
