package com.nischint.app.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nischint.app.ui.theme.*

/**
 * Neomorphic card with soft shadows
 */
@Composable
fun NeomorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    backgroundColor: Color = Surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowDark.copy(alpha = 0.5f),
                spotColor = ShadowDark.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Neomorphic button with press effect
 */
@Composable
fun NeomorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = GoldPrimary,
    contentColor: Color = TextOnGold,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val elevation = if (isPressed) 2.dp else 6.dp
    
    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowDark.copy(alpha = 0.4f),
                spotColor = ShadowDark.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Neomorphic icon button (circular)
 */
@Composable
fun NeomorphicIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    backgroundColor: Color = Surface,
    iconColor: Color = GoldPrimary,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val elevation = if (isPressed) 2.dp else 6.dp
    
    Surface(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = ShadowDark.copy(alpha = 0.4f),
                spotColor = ShadowDark.copy(alpha = 0.4f)
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CircleShape,
        color = backgroundColor
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
 * Neomorphic inset (pressed in) container
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
                elevation = 4.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = ShadowLight,
                spotColor = ShadowLight
            )
            .background(
                color = Background.copy(alpha = 0.8f),
                shape = RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

/**
 * Section header with title
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
            color = TextPrimary
        )
        action?.invoke()
    }
}

/**
 * Money display with currency symbol
 */
@Composable
fun MoneyText(
    amount: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge,
    color: Color = TextPrimary,
    showSign: Boolean = false,
    isExpense: Boolean = true
) {
    val displayAmount = if (showSign) {
        if (isExpense) "-₹$amount" else "+₹$amount"
    } else {
        "₹$amount"
    }
    
    val displayColor = if (showSign) {
        if (isExpense) RedDanger else TealSafe
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
 * Risk level badge
 */
@Composable
fun RiskBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (level.lowercase()) {
        "green" -> TealSafe to "Safe ✓"
        "yellow" -> OrangeWarning to "Caution ⚠"
        "red" -> RedDanger to "Alert ⚠"
        else -> TealSafe to "Safe ✓"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
