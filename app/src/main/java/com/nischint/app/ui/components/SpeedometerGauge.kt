package com.nischint.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nischint.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Speedometer gauge showing "Safe to Spend" amount
 * 
 * @param safeToSpend Current safe amount in rupees
 * @param maxAmount Maximum amount for the gauge (default 10000)
 * @param riskLevel Current risk level: "green", "yellow", "red"
 */
@Composable
fun SpeedometerGauge(
    safeToSpend: Int,
    maxAmount: Int = 10000,
    riskLevel: String = "green",
    modifier: Modifier = Modifier,
    size: Dp = 280.dp
) {
    // Animate the needle
    val progress = (safeToSpend.toFloat() / maxAmount).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "needle_animation"
    )
    
    val riskColor = getRiskColor(riskLevel)
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Draw the gauge arc
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 24.dp.toPx()
            val arcSize = Size(size.toPx() - strokeWidth, size.toPx() - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            
            // Background arc (gray)
            drawArc(
                color = ShadowDark.copy(alpha = 0.3f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Gradient arc (Red -> Yellow -> Green from left to right)
            val gradientBrush = Brush.sweepGradient(
                0f to RedDanger,
                0.3f to OrangeWarning,
                0.6f to GoldPrimary,
                1f to TealSafe,
                center = Offset(size.toPx() / 2, size.toPx() / 2)
            )
            
            // Draw colored segments
            // Red zone (0-30%)
            drawArc(
                color = RedDanger,
                startAngle = 150f,
                sweepAngle = 72f,  // 30% of 240
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Yellow zone (30-60%)
            drawArc(
                color = OrangeWarning,
                startAngle = 222f,
                sweepAngle = 72f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            
            // Green zone (60-100%)
            drawArc(
                color = TealSafe,
                startAngle = 294f,
                sweepAngle = 96f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw the needle
            val needleAngle = 150f + (animatedProgress * 240f)
            val needleLength = (size.toPx() / 2) - strokeWidth - 20.dp.toPx()
            val centerX = size.toPx() / 2
            val centerY = size.toPx() / 2
            
            // Needle shadow
            rotate(needleAngle + 2f, pivot = Offset(centerX, centerY)) {
                drawLine(
                    color = ShadowDark.copy(alpha = 0.3f),
                    start = Offset(centerX, centerY),
                    end = Offset(centerX, centerY - needleLength),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Main needle
            rotate(needleAngle, pivot = Offset(centerX, centerY)) {
                drawLine(
                    color = riskColor,
                    start = Offset(centerX, centerY),
                    end = Offset(centerX, centerY - needleLength),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Center circle
            drawCircle(
                color = Surface,
                radius = 20.dp.toPx(),
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = riskColor,
                radius = 12.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 30.dp)
        ) {
            Text(
                text = "₹$safeToSpend",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = riskColor
            )
            Text(
                text = "Safe to Spend",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

/**
 * Simplified gauge for smaller displays
 */
@Composable
fun MiniGauge(
    value: Int,
    maxValue: Int,
    riskLevel: String,
    modifier: Modifier = Modifier
) {
    val progress = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val riskColor = getRiskColor(riskLevel)
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mini arc indicator
        Canvas(modifier = Modifier.size(40.dp)) {
            val strokeWidth = 4.dp.toPx()
            val arcSize = Size(40.dp.toPx() - strokeWidth, 40.dp.toPx() - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            
            // Background
            drawArc(
                color = ShadowDark.copy(alpha = 0.3f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Progress
            drawArc(
                color = riskColor,
                startAngle = 150f,
                sweepAngle = 240f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column {
            Text(
                text = "₹$value",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = riskColor
            )
            Text(
                text = "Safe",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
