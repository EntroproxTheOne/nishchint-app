package com.nischint.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nischint.app.data.api.MockData
import com.nischint.app.data.models.OnboardingQuestion
import com.nischint.app.ui.components.NeomorphicButton
import com.nischint.app.ui.components.NeomorphicCard
import com.nischint.app.ui.theme.*
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    NischintTheme {
        OnboardingScreen(onComplete = {})
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var showHoroscope by remember { mutableStateOf(false) }
    var isAgentTyping by remember { mutableStateOf(false) }
    
    val questions = MockData.onboardingQuestions
    val listState = rememberLazyListState()
    
    // Add initial greeting
    LaunchedEffect(Unit) {
        delay(500)
        isAgentTyping = true
        delay(1000)
        isAgentTyping = false
        messages = messages + ChatMessage(
            text = "Namaste! 🙏 Main Nischint hoon, tera financial buddy!",
            isUser = false
        )
        delay(800)
        isAgentTyping = true
        delay(800)
        isAgentTyping = false
        messages = messages + ChatMessage(
            text = questions[0].text,
            isUser = false
        )
    }
    
    // Scroll to bottom when new messages added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Background, Surface)
                )
            )
    ) {
        // Header
        OnboardingHeader()
        
        // Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
            
            // Typing indicator
            if (isAgentTyping) {
                item {
                    TypingIndicator()
                }
            }
        }
        
        // Options or Horoscope
        AnimatedContent(
            targetState = showHoroscope,
            transitionSpec = {
                fadeIn() + slideInVertically { it } togetherWith fadeOut() + slideOutVertically { -it }
            },
            label = "bottom_content"
        ) { showingHoroscope ->
            if (showingHoroscope) {
                HoroscopeCard(
                    horoscope = MockData.horoscope,
                    onContinue = onComplete
                )
            } else if (currentQuestionIndex < questions.size) {
                OptionsPanel(
                    options = questions[currentQuestionIndex].options,
                    onOptionSelected = { option ->
                        // Add user message
                        messages = messages + ChatMessage(text = option, isUser = true)
                        
                        // Move to next question or show horoscope
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            // Add agent response after delay
                            isAgentTyping = true
                            // The next question will be added via LaunchedEffect
                        } else {
                            showHoroscope = true
                        }
                    },
                    enabled = !isAgentTyping
                )
            }
        }
    }
    
    // Handle showing next question
    LaunchedEffect(currentQuestionIndex, messages.size) {
        if (isAgentTyping && currentQuestionIndex > 0 && currentQuestionIndex < questions.size) {
            delay(1200)
            isAgentTyping = false
            messages = messages + ChatMessage(
                text = questions[currentQuestionIndex].text,
                isUser = false
            )
        }
    }
}

@Composable
fun OnboardingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Agent Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GoldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🤖",
                fontSize = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = "Nischint",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Tera Financial Buddy",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) GoldPrimary else Surface,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isUser) TextOnGold else TextPrimary,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "dot_$index")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha_$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TextSecondary.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun OptionsPanel(
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            OptionButton(
                text = option,
                onClick = { onOptionSelected(option) },
                enabled = enabled
            )
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) Surface else Surface.copy(alpha = 0.5f),
        shadowElevation = if (enabled) 4.dp else 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) TextPrimary else TextSecondary,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HoroscopeCard(
    horoscope: com.nischint.app.data.models.HoroscopeResponse,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GoldLight.copy(alpha = 0.3f), GoldPrimary.copy(alpha = 0.5f))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔮",
            fontSize = 48.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tera Financial Horoscope",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        NeomorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Surface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Predicted Expense",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = horoscope.predictedExpense,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Savings Potential",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = horoscope.savingsPotential,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TealSafe
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "💡 ${horoscope.tip}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        NeomorphicButton(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GoldPrimary
        ) {
            Text(
                text = "Chalo Shuru Karte Hai! 🚀",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
