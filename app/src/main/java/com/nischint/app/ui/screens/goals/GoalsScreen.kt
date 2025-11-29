package com.nischint.app.ui.screens.goals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nischint.app.data.api.MockData
import com.nischint.app.ui.components.*
import com.nischint.app.ui.theme.*

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
fun GoalsScreenPreview() {
    NischintTheme {
        GoalsScreen()
    }
}

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showScratchCard by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadGoal()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "🎯 Goals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite  // Updated to TextWhite
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Bike Progress Card
        uiState.goal?.let { goal ->
            BikeProgressCard(
                goalName = goal.name,
                saved = goal.saved,
                target = goal.target,
                progress = goal.progress
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Streak Card
            StreakCard(
                streakDays = goal.streakDays,
                onAddSavings = { viewModel.addSavings(50) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Scratch Card
            if (goal.streakDays >= 3 && !uiState.scratchCardRevealed) {
                ScratchCardTrigger(
                    onClick = { showScratchCard = true }
                )
            }
            
            if (uiState.scratchCardRevealed) {
                RewardCard(reward = uiState.reward)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tips Section
        TipsCard()
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
    }
    
    // Scratch Card Dialog
    if (showScratchCard) {
        ScratchCardDialog(
            onDismiss = { showScratchCard = false },
            onReveal = { 
                viewModel.revealScratchCard()
                showScratchCard = false
            }
        )
    }
}

@Composable
fun BikeProgressCard(
    goalName: String,
    saved: Int,
    target: Int,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "bike_progress"
    )
    
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goalName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite  // Updated to TextWhite
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bike Illustration with Fill
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                BikeProgressIllustration(
                    progress = animatedProgress,
                    modifier = Modifier.size(180.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress Text
            Text(
                text = "₹$saved",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
            Text(
                text = "of ₹$target",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray  // Updated to TextGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = GoldPrimary,
                trackColor = ShadowDark.copy(alpha = 0.2f),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(animatedProgress * 100).toInt()}% complete",
                style = MaterialTheme.typography.labelMedium,
                color = TextGray  // Updated to TextGray
            )
        }
    }
}

@Composable
fun BikeProgressIllustration(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Background circle
        drawCircle(
            color = ShadowDark.copy(alpha = 0.1f),
            radius = width / 2
        )
        
        // Fill from bottom based on progress
        val fillHeight = height * progress
        
        clipRect(
            left = 0f,
            top = height - fillHeight,
            right = width,
            bottom = height
        ) {
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(GoldLight, GoldPrimary)
                ),
                radius = width / 2
            )
        }
        
        // Draw simple bike icon
        val bikeColor = if (progress > 0.5f) Color.White else TextPrimary
        
        // Wheels
        val wheelRadius = width * 0.15f
        val wheelY = height * 0.65f
        
        // Left wheel
        drawCircle(
            color = bikeColor,
            radius = wheelRadius,
            center = Offset(width * 0.3f, wheelY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
        
        // Right wheel
        drawCircle(
            color = bikeColor,
            radius = wheelRadius,
            center = Offset(width * 0.7f, wheelY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
        
        // Frame
        val framePath = Path().apply {
            moveTo(width * 0.3f, wheelY)
            lineTo(width * 0.5f, height * 0.35f)
            lineTo(width * 0.7f, wheelY)
            moveTo(width * 0.5f, height * 0.35f)
            lineTo(width * 0.4f, height * 0.5f)
            lineTo(width * 0.3f, wheelY)
        }
        
        drawPath(
            path = framePath,
            color = bikeColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        
        // Handlebar
        drawLine(
            color = bikeColor,
            start = Offset(width * 0.5f, height * 0.35f),
            end = Offset(width * 0.6f, height * 0.3f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        
        // Seat
        drawLine(
            color = bikeColor,
            start = Offset(width * 0.4f, height * 0.5f),
            end = Offset(width * 0.35f, height * 0.45f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun StreakCard(
    streakDays: Int,
    onAddSavings: () -> Unit
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = OrangeWarning,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "$streakDays din ka streak!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite  // Updated to TextWhite
                    )
                    Text(
                        text = "Aaj bhi ₹50 bachao",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray  // Updated to TextGray
                    )
                }
            }
            
            FilledTonalButton(
                onClick = onAddSavings,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GoldLight.copy(alpha = 0.3f)
                )
            ) {
                Text("+₹50", color = GoldDark)
            }
        }
    }
}

@Composable
fun ScratchCardTrigger(
    onClick: () -> Unit
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = GoldLight.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎟️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "3-Day Streak Reward!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            NeomorphicButton(
                onClick = onClick
                // Using default yellow background
            ) {
                Text("Scratch Karo!")
            }
        }
    }
}

@Composable
fun ScratchCardDialog(
    onDismiss: () -> Unit,
    onReveal: () -> Unit
) {
    var scratchProgress by remember { mutableStateOf(0f) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = "🎟️ Scratch Card",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Scratch karo reward dekhne ke liye!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray  // Updated to TextGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Simple scratch simulation
                Box(
                    modifier = Modifier
                        .size(200.dp, 100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (scratchProgress < 0.5f) ShadowDark 
                            else GoldLight
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { _, _ ->
                                scratchProgress = (scratchProgress + 0.1f).coerceAtMost(1f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (scratchProgress >= 0.5f) {
                        Text(
                            text = "₹10 Cashback! 🎉",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    } else {
                        Text(
                            text = "👆 Scratch here",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextOnGold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { scratchProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = GoldPrimary,
                )
            }
        },
        confirmButton = {
            if (scratchProgress >= 0.5f) {
                Button(
                    onClick = onReveal,
                    colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                ) {
                    Text("Claim Reward!")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later", color = TextSecondary)
            }
        }
    )
}

@Composable
fun RewardCard(
    reward: String
) {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TealSafe.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Reward Claimed!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealDark
                )
                Text(
                    text = reward,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray  // Updated to TextGray
                )
            }
        }
    }
}

@Composable
fun TipsCard() {
    NeomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "💡 Savings Tip",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite  // Updated to TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Roz ₹50 bachao, 6 mahine mein ₹9,000! Bike ka down payment almost ready! 🏍️",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray  // Updated to TextGray
            )
        }
    }
}
