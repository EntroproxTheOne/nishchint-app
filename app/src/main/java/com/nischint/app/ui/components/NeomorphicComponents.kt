package com.nischint.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nischint.app.ui.theme.*
import com.nischint.app.ui.utils.rememberHapticFeedback
import com.nischint.app.ui.utils.HapticType

/**
 * Neomorphic card with dark background and subtle yellow border
 */
@Composable
fun NeomorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 4.dp,  // Reduced for flatter, modern look
    backgroundColor: Color = SurfaceDark,  // Dark surface
    borderColor: Color = BorderSubtle,  // Subtle border
    showBorder: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowColor.copy(alpha = 0.3f),
                spotColor = ShadowColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        border = if (showBorder) androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = borderColor
        ) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Modern button with flat design and yellow fill
 */
@Composable
fun NeomorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 20.dp,  // More rounded for modern look
    backgroundColor: Color = YellowPrimary,  // Yellow by default
    contentColor: Color = TextOnYellow,  // Dark text on yellow
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val hapticFeedback = rememberHapticFeedback()
    
    // Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )
    
    // Flatter elevation for modern design
    val elevation = if (isPressed) 1.dp else 3.dp
    
    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowColor.copy(alpha = 0.3f),
                spotColor = ShadowColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    hapticFeedback(HapticType.MEDIUM_CLICK)
                    onClick()
                }
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 16.dp),  // Slightly more padding
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Neomorphic icon button (circular) - Dark theme
 */
@Composable
fun NeomorphicIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    backgroundColor: Color = SurfaceDark,  // Dark background
    iconColor: Color = YellowPrimary,  // Yellow icon
    contentDescription: String? = null,
    hapticType: HapticType = HapticType.LIGHT_CLICK
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val hapticFeedback = rememberHapticFeedback()
    
    // Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "icon_button_scale"
    )
    
    val elevation = if (isPressed) 1.dp else 3.dp  // Reduced for modern look
    
    Surface(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = ShadowColor.copy(alpha = 0.5f),
                spotColor = ShadowColor.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    hapticFeedback(hapticType)
                    onClick()
                }
            ),
        shape = CircleShape,
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = BorderSubtle
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

/**
 * Neomorphic inset (pressed in) container - Dark theme
 */
@Composable
fun NeomorphicInset(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowColor.copy(alpha = 0.6f),
                spotColor = ShadowColor.copy(alpha = 0.6f)
            )
            .background(
                color = BackgroundDark.copy(alpha = 0.8f),  // Dark background
                shape = RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

/**
 * Section header with title - Dark theme
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextWhite  // White text for dark theme
        )
        action?.invoke()
    }
}

/**
 * Money display with currency symbol - Dark theme
 */
@Composable
fun MoneyText(
    amount: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge,
    color: Color = YellowPrimary,  // Yellow for money by default
    showSign: Boolean = false,
    isExpense: Boolean = true
) {
    val displayAmount = if (showSign) {
        if (isExpense) "-₹$amount" else "+₹$amount"
    } else {
        "₹$amount"
    }
    
    val displayColor = if (showSign) {
        if (isExpense) RedDanger else GreenSafe  // Green for income
    } else {
        color
    }
    
    Text(
        text = displayAmount,
        style = style,
        color = displayColor,
        modifier = modifier
    )
}

/**
 * Risk level badge - Dark theme
 */
@Composable
fun RiskBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (level.lowercase()) {
        "green" -> GreenSafe to "Safe ✓"  // Updated to GreenSafe
        "yellow" -> OrangeWarning to "Caution ⚠"
        "red" -> RedDanger to "Alert ⚠"
        else -> GreenSafe to "Safe ✓"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.2f),  // Slightly more opaque for dark theme
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
