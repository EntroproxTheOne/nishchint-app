package com.nischint.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NischintColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = TextOnGold,
    primaryContainer = GoldLight,
    secondary = TealSafe,
    onSecondary = TextOnGold,
    secondaryContainer = TealDark,
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = RedDanger,
    onError = TextOnGold
)

@Composable
fun NischintTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = NischintColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NischintTypography,
        content = content
    )
}
