# CLAUDE.md - Nischint Project Instructions

## Project Overview

**Nischint** is a financial coaching Android app for India's informal sector workers (gig workers, delivery partners, daily wage earners). The name means "worry-free" in Hindi.

**Target User:** Rohan, a 24-year-old Zomato delivery partner in Mumbai earning ₹20-30k/month irregularly. He wants to save for a bike upgrade but struggles with EMIs, fuel costs, and unpredictable income.

**Core Value:** Convert irregular income into predictable savings through AI-powered coaching.

---

## Tech Stack

### Android App
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Min SDK:** 26 (Android 8.0)
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Navigation:** Jetpack Compose Navigation
- **HTTP Client:** Retrofit 2 + OkHttp
- **State Management:** ViewModel + remember/mutableStateOf

### Backend
- **Framework:** FastAPI (Python)
- **ML:** scikit-learn (HistGradientBoostingRegressor)
- **Port:** 8000

---

## Design System

### Colors (Use these exact values)
```kotlin
val GoldPrimary = Color(0xFFF5A623)      // Main brand color
val GoldLight = Color(0xFFFFD93D)        // Highlights
val TealSafe = Color(0xFF00B894)         // "Safe" state
val OrangeWarning = Color(0xFFFF6B35)    // Warning state
val RedDanger = Color(0xFFE74C3C)        // Danger/EMI due

val Background = Color(0xFFE8E8E8)       // App background
val Surface = Color(0xFFF0F0F0)          // Card surface
val ShadowDark = Color(0xFFBEBEBE)       // Neomorphic shadow
val TextPrimary = Color(0xFF2D3436)      // Main text
val TextSecondary = Color(0xFF636E72)    // Secondary text
```

### Typography
- **Font Family:** Poppins (or system default if not available)
- **Headings:** Bold, larger sizes
- **Body:** Regular weight
- **Numbers/Money:** Semi-bold, slightly larger

### UI Style: Neomorphism
- Soft shadows on both sides (dark bottom-right, light top-left)
- Rounded corners (16-24dp)
- Subtle, not aggressive
- Background should be light gray, not white

---

## App Structure

### Screens (4 total)

1. **OnboardingScreen** - First-time user flow
   - Akinator-style chat interface
   - Questions appear as chat bubbles
   - User taps options (not typing)
   - Ends with "Financial Horoscope" prediction
   - Hindi/Hinglish text preferred

2. **HomeScreen** - Main dashboard
   - Speedometer gauge showing "Safe to Spend" amount
   - Green/Yellow/Red zones based on financial health
   - Floating mic button (center-bottom, above nav)
   - Prediction card showing next week's forecast
   - "Add Cash" button for income logging

3. **TrackerScreen** - Transaction history
   - Toggle: "Dhanda" (Business) | "Ghar" (Personal)
   - "Simulate SMS Sync" button for demo
   - Transaction list grouped by date
   - Each item: icon, merchant, amount, category
   - Auto-categorization badges

4. **GoalsScreen** - Savings goals
   - Large bike image that fills with color as savings grow
   - Progress bar with amount saved / target
   - Streak counter ("🔥 3-day streak!")
   - Scratch card reward component

### Navigation
- Bottom navigation bar with 3 items: Home, Tracker, Goals
- Floating mic button overlays the nav bar
- Onboarding shown only on first launch

---

## Component Guidelines

### SpeedometerGauge
```kotlin
@Composable
fun SpeedometerGauge(
    safeToSpend: Int,           // Amount in rupees
    maxAmount: Int = 10000,     // Max for gauge
    riskLevel: RiskLevel        // GREEN, YELLOW, RED
)
```
- Semi-circle gauge
- Needle points to current safe amount
- Color gradient: Red (left) → Yellow (center) → Green (right)
- Large number in center: "₹2,500"
- Label below: "Safe to Spend"

### AgentMicButton
```kotlin
@Composable
fun AgentMicButton(
    onClick: () -> Unit,
    isListening: Boolean = false
)
```
- Circular button, 64dp size
- Gold background
- White mic icon
- Pulse animation when listening
- Positioned above bottom nav, centered

### TransactionItem
```kotlin
@Composable
fun TransactionItem(
    transaction: Transaction
)
```
- Horizontal card
- Left: Category icon (emoji or vector)
- Center: Merchant name + category tag
- Right: Amount (red for expense, green for income)
- Neomorphic card style

### ProgressBike
```kotlin
@Composable
fun ProgressBike(
    progress: Float  // 0.0 to 1.0
)
```
- Bike outline/silhouette
- Fills from bottom with gold color based on progress
- Can use gradient or clip animation

---

## API Integration

### Base URL Configuration
```kotlin
object ApiConfig {
    // For emulator
    const val EMULATOR_URL = "http://10.0.2.2:8000"
    // For real device (update with ngrok URL)
    const val DEVICE_URL = "https://xxxx.ngrok.io"
    
    val BASE_URL = EMULATOR_URL  // Change for testing
}
```

### Retrofit Service
```kotlin
interface NischintApi {
    @GET("api/user/dashboard")
    suspend fun getDashboard(): DashboardResponse
    
    @POST("api/sms/simulate")
    suspend fun simulateSms(): SmsResponse
    
    @POST("api/onboarding/answer")
    suspend fun submitAnswer(@Body answer: AnswerRequest): QuestionResponse
    
    @GET("api/onboarding/horoscope")
    suspend fun getHoroscope(): HoroscopeResponse
}
```

### Mock Data (Use when backend unavailable)
```kotlin
object MockData {
    val dashboard = DashboardResponse(
        name = "Rohan",
        safeToSpend = 2500,
        riskLevel = "green",
        prediction = Prediction(
            expenseLow = 12000,
            expenseHigh = 14500,
            confidence = 0.90f,
            message = "Stable week expected"
        ),
        goal = Goal(
            name = "Hero Splendor",
            target = 35000,
            saved = 12250,
            progress = 0.35f,
            streakDays = 3
        )
    )
    
    val transactions = listOf(
        Transaction("1", "expense", 150, "food", "Zomato", false),
        Transaction("2", "expense", 200, "fuel", "HP Petrol", true),
        Transaction("3", "income", 100, "tips", "Customer Tip", true),
        Transaction("4", "expense", 50, "food", "Chai Tapri", false),
        Transaction("5", "expense", 320, "grocery", "D-Mart", false)
    )
}
```

---

## Voice Commands (Hardcoded for Demo)

When user speaks, check for these keywords:
```kotlin
fun handleVoiceCommand(text: String, navController: NavController) {
    val lower = text.lowercase()
    when {
        lower.contains("bike") || lower.contains("goal") -> 
            navController.navigate("goals")
        lower.contains("income") || lower.contains("cash") || lower.contains("paisa") -> 
            showAddCashDialog = true
        lower.contains("kharcha") || lower.contains("expense") || lower.contains("tracker") -> 
            navController.navigate("tracker")
        lower.contains("home") || lower.contains("ghar") -> 
            navController.navigate("home")
    }
}
```

---

## Code Style Guidelines

### Compose Best Practices
1. Use `remember` and `mutableStateOf` for local UI state
2. Use `collectAsStateWithLifecycle()` for ViewModel states
3. Extract reusable components to separate files
4. Use `Modifier` as first parameter for composables
5. Preview every component with `@Preview`

### Naming Conventions
- Screens: `XxxScreen.kt` (e.g., `HomeScreen.kt`)
- ViewModels: `XxxViewModel.kt`
- Components: `XxxComponent.kt` or just `Xxx.kt`
- Use PascalCase for composables
- Use camelCase for functions and variables

### File Organization
```kotlin
// 1. Package declaration
package com.nischint.app.ui.screens.home

// 2. Imports (alphabetized)

// 3. Preview (at top for visibility)
@Preview
@Composable
fun HomeScreenPreview() { ... }

// 4. Main composable
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigate: (String) -> Unit
) { ... }

// 5. Helper composables (private)
@Composable
private fun SomeHelper() { ... }
```

---

## Common Patterns

### ViewModel Pattern
```kotlin
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadDashboard() {
        viewModelScope.launch {
            try {
                val data = repository.getDashboard()
                _uiState.update { it.copy(dashboard = data, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}

data class HomeUiState(
    val dashboard: DashboardResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
```

### Navigation Setup
```kotlin
@Composable
fun NischintNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(onNavigate = { navController.navigate(it) }) }
            composable("tracker") { TrackerScreen() }
            composable("goals") { GoalsScreen() }
        }
    }
}
```

---

## Localization (Hindi Strings)

Use Hindi/Hinglish for user-facing text:
```kotlin
object Strings {
    const val APP_NAME = "Nischint"
    const val SAFE_TO_SPEND = "Kharcha Limit"
    const val ADD_CASH = "Paisa Add Karo"
    const val BUSINESS = "Dhanda"
    const val PERSONAL = "Ghar"
    const val SIMULATE_SYNC = "SMS Sync Karo"
    const val GOAL_BIKE = "Meri Bike"
    const val STREAK = "din ka streak! 🔥"
    const val SCRATCH_CARD = "Scratch Karo!"
    const val PREDICTION = "Agle Hafte"
    const val HOROSCOPE_TITLE = "Tera Financial Horoscope"
}
```

---

## Testing Checklist

Before demo, verify:
- [ ] App launches without crash
- [ ] Bottom navigation works (all 3 tabs)
- [ ] Home screen shows speedometer with number
- [ ] Mic button is visible and tappable
- [ ] Voice command "bike" navigates to Goals
- [ ] Tracker shows "Simulate SMS" button
- [ ] Clicking simulate populates transaction list
- [ ] Goals shows bike with progress fill
- [ ] Scratch card popup works
- [ ] No ANR (app not responding) errors

---

## Emergency Fallbacks

### If API fails:
```kotlin
// In ViewModel
fun loadDashboard() {
    viewModelScope.launch {
        try {
            val data = api.getDashboard()
            _uiState.update { it.copy(dashboard = data) }
        } catch (e: Exception) {
            // FALLBACK TO MOCK DATA
            _uiState.update { it.copy(dashboard = MockData.dashboard) }
        }
    }
}
```

### If voice recognition fails:
- Show manual input dialog
- Or hardcode a successful demo path

### If time runs out:
- Priority 1: Home screen (must be perfect)
- Priority 2: Tracker with mock data
- Priority 3: Goals with static progress
- Skip: Complex animations, real voice recognition

---

## Commands for Claude Code

When working on this project, you can ask:

1. "Create the HomeScreen with speedometer gauge"
2. "Add bottom navigation with 3 tabs"
3. "Create neomorphic card component"
4. "Setup Retrofit for API calls"
5. "Create transaction list for tracker"
6. "Add bike progress animation for goals"
7. "Implement voice recognition with SpeechRecognizer"
8. "Create onboarding chat interface"
9. "Fix [paste error message]"
10. "Make this component match the design system"

---

## Final Notes

This is a **hackathon project**. Prioritize:
1. **Working > Perfect** - A working demo beats polished but broken
2. **Visual impact** - Judges remember what looks good
3. **Story** - Rohan's journey should be clear
4. **Technical depth** - Show ML integration prominently

The speedometer on home screen is the HERO component. Make it beautiful.

Good luck! 🚀
