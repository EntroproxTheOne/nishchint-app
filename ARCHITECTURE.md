# Nischint - Native Android Architecture

## 🎯 Tech Stack (Optimized for 10-Hour Hackathon)

### Frontend: Native Android
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Navigation:** Compose Navigation
- **State:** ViewModel + StateFlow
- **HTTP:** Retrofit + OkHttp
- **DI:** Manual (no Hilt/Dagger for speed)

### Backend: FastAPI (Python)
- **Why:** Your ML model + SMS parser are already Python
- **Hosting:** Run locally on laptop, use ngrok for phone access
- **Time to setup:** 30 minutes

---

## 📁 Complete Project Structure

```
nischint-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/nischint/app/
│   │   │   │
│   │   │   ├── MainActivity.kt              # Entry point
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   └── NischintNavigation.kt    # Bottom nav + routing
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt             # Gold/Teal palette
│   │   │   │   │   ├── Theme.kt             # Material 3 theme
│   │   │   │   │   ├── Type.kt              # Typography
│   │   │   │   │   └── Shape.kt             # Neomorphic shapes
│   │   │   │   │
│   │   │   │   ├── components/              # REUSABLE COMPONENTS
│   │   │   │   │   ├── NeomorphicCard.kt
│   │   │   │   │   ├── NeomorphicButton.kt
│   │   │   │   │   ├── SpeedometerGauge.kt  # The main meter
│   │   │   │   │   ├── AgentMicButton.kt    # Floating mic
│   │   │   │   │   ├── TransactionItem.kt
│   │   │   │   │   └── ProgressBike.kt      # Filling bike SVG
│   │   │   │   │
│   │   │   │   ├── screens/                 # MAIN SCREENS
│   │   │   │   │   ├── onboarding/
│   │   │   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   │   │   ├── ChatBubble.kt
│   │   │   │   │   │   └── FinancialHoroscope.kt
│   │   │   │   │   │
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   │   └── PredictionCard.kt
│   │   │   │   │   │
│   │   │   │   │   ├── tracker/
│   │   │   │   │   │   ├── TrackerScreen.kt
│   │   │   │   │   │   ├── TrackerViewModel.kt
│   │   │   │   │   │   └── CategoryToggle.kt
│   │   │   │   │   │
│   │   │   │   │   └── goals/
│   │   │   │   │       ├── GoalsScreen.kt
│   │   │   │   │       ├── GoalsViewModel.kt
│   │   │   │   │       └── ScratchCard.kt
│   │   │   │   │
│   │   │   │   └── dialogs/
│   │   │   │       ├── AddCashDialog.kt
│   │   │   │       └── VoiceInputDialog.kt
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── models/                  # DATA CLASSES
│   │   │   │   │   ├── User.kt
│   │   │   │   │   ├── Transaction.kt
│   │   │   │   │   ├── Prediction.kt
│   │   │   │   │   └── Goal.kt
│   │   │   │   │
│   │   │   │   ├── repository/
│   │   │   │   │   ├── UserRepository.kt
│   │   │   │   │   ├── TransactionRepository.kt
│   │   │   │   │   └── PredictionRepository.kt
│   │   │   │   │
│   │   │   │   └── api/
│   │   │   │       ├── ApiService.kt        # Retrofit interface
│   │   │   │       ├── ApiClient.kt         # Retrofit setup
│   │   │   │       └── MockData.kt          # HARDCODED DEMO DATA
│   │   │   │
│   │   │   └── utils/
│   │   │       ├── VoiceRecognition.kt
│   │   │       └── Extensions.kt
│   │   │
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   │   ├── ic_bike.xml              # Bike vector
│   │   │   │   ├── ic_home.xml
│   │   │   │   ├── ic_tracker.xml
│   │   │   │   ├── ic_goals.xml
│   │   │   │   └── ic_mic.xml
│   │   │   │
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   │
│   │   │   └── font/
│   │   │       └── poppins.ttf
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── backend/                                  # FastAPI Backend
│   ├── app/
│   │   ├── main.py
│   │   ├── routers/
│   │   │   ├── prediction.py
│   │   │   ├── sms.py
│   │   │   └── onboarding.py
│   │   ├── models/
│   │   │   ├── ml_model.py                  # Your existing POC
│   │   │   └── sms_parser.py                # Your existing script
│   │   └── data/
│   │       └── sample_sms.txt
│   ├── requirements.txt
│   └── run.sh
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── CLAUDE.md                                 # Instructions for Claude Code
└── README.md
```

---

## 🎨 Design System

### Color Palette
```kotlin
// Color.kt
object NischintColors {
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
    
    // Neomorphism Base (Light Theme)
    val Background = Color(0xFFE8E8E8)
    val Surface = Color(0xFFF0F0F0)
    val ShadowDark = Color(0xFFBEBEBE)
    val ShadowLight = Color(0xFFFFFFFF)
    
    // Text
    val TextPrimary = Color(0xFF2D3436)
    val TextSecondary = Color(0xFF636E72)
}
```

### Neomorphic Modifier Extension
```kotlin
// NeomorphicModifier.kt
fun Modifier.neomorphic(
    elevation: Dp = 8.dp,
    cornerRadius: Dp = 20.dp
) = this
    .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = NischintColors.ShadowDark,
        spotColor = NischintColors.ShadowDark
    )
    .background(
        color = NischintColors.Surface,
        shape = RoundedCornerShape(cornerRadius)
    )
```

---

## 🔌 API Contracts

### Base URL
```
DEV:  http://10.0.2.2:8000  (Android Emulator)
PROD: https://your-ngrok-url.ngrok.io
```

### 1. GET /api/user/dashboard
```json
Response:
{
  "name": "Rohan",
  "safe_to_spend": 2500,
  "risk_level": "green",
  "prediction": {
    "expense_low": 12000,
    "expense_high": 14500,
    "confidence": 0.90,
    "message": "Expected stable week ahead"
  },
  "goal": {
    "name": "Hero Splendor",
    "target": 35000,
    "saved": 12250,
    "progress": 0.35,
    "streak_days": 3
  }
}
```

### 2. POST /api/sms/simulate
```json
Response:
{
  "transactions": [
    {
      "id": "txn_1",
      "type": "expense",
      "amount": 150,
      "category": "food",
      "merchant": "Zomato",
      "timestamp": "2024-11-28T14:30:00",
      "is_business": false
    }
  ]
}
```

### 3. POST /api/onboarding/answer
```json
Request:
{
  "question_id": "q2",
  "answer": "yes"
}

Response:
{
  "next_question": {
    "id": "q3",
    "text": "Kya aapke paas koi loan ya EMI hai?",
    "options": ["Haan", "Nahi"]
  },
  "is_complete": false
}
```

### 4. GET /api/onboarding/horoscope
```json
Response:
{
  "predicted_expense": "₹12,000 - ₹14,500",
  "savings_potential": "₹3,500",
  "risk_areas": ["Fuel costs high", "Food delivery frequent"],
  "tip": "₹40/day SIP mein daalo, 1 saal mein bike ka down payment!"
}
```

---

## 👥 Parallel Development - WHO DOES WHAT

### 🔧 Person 1: Backend + ML Integration
**Owner of:** `backend/` folder entirely

**Tasks:**
1. FastAPI skeleton with mock endpoints (Hour 0-1)
2. Integrate your ML model for prediction (Hour 1-3)
3. Integrate SMS parser script (Hour 3-4)
4. Onboarding question flow logic (Hour 4-5)
5. Test all endpoints with Postman/curl (Hour 5-6)
6. ngrok setup for phone testing (Hour 6)
7. Demo path optimization (Hour 6-10)

**Files:**
- `backend/app/main.py`
- `backend/app/routers/*.py`
- `backend/app/models/*.py`

**Start command:**
```bash
cd backend
pip install fastapi uvicorn pandas scikit-learn
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

---

### 📱 Person 2: App Shell + Home Screen + Navigation
**Owner of:** Core app structure, Home screen, Navigation

**Tasks:**
1. Android Studio project setup (Hour 0-0.5)
2. Theme + Colors + Typography (Hour 0.5-1)
3. Bottom Navigation setup (Hour 1-2)
4. Home Screen layout (Hour 2-4)
5. Speedometer Gauge component (Hour 4-5)
6. Agent Mic Button (Hour 5-6)
7. API integration with Retrofit (Hour 6-8)
8. Voice recognition basic (Hour 8-9)
9. Polish + testing (Hour 9-10)

**Files:**
- `MainActivity.kt`
- `navigation/NischintNavigation.kt`
- `ui/theme/*`
- `ui/screens/home/*`
- `ui/components/SpeedometerGauge.kt`
- `ui/components/AgentMicButton.kt`
- `data/api/*`

---

### 🎨 Person 3: Tracker + Goals + Onboarding
**Owner of:** Tracker screen, Goals screen, Onboarding flow

**Tasks:**
1. Tracker Screen layout (Hour 0-2)
2. Transaction list + Category toggle (Hour 2-3)
3. Goals Screen layout (Hour 3-4)
4. Bike progress animation (Hour 4-5)
5. Scratch card component (Hour 5-6)
6. Onboarding chat interface (Hour 6-8)
7. Onboarding question flow (Hour 8-9)
8. Polish animations (Hour 9-10)

**Files:**
- `ui/screens/tracker/*`
- `ui/screens/goals/*`
- `ui/screens/onboarding/*`
- `ui/components/TransactionItem.kt`
- `ui/components/ProgressBike.kt`
- `ui/dialogs/ScratchCard.kt`

---

## 🔀 Git Strategy - NEVER CONFLICT

### Branch Structure
```
main
├── backend              # Person 1 ONLY
├── app-shell            # Person 2 ONLY
└── screens              # Person 3 ONLY
```

### Merge Schedule
- Hour 3: First merge (basic structure)
- Hour 6: Second merge (features connected)
- Hour 9: Final merge (polish)

### Golden Rules
1. **Person 2 owns `MainActivity.kt` and `NischintNavigation.kt`**
2. **Person 3 creates screens, Person 2 adds routes**
3. **Use `MockData.kt` until backend is ready**
4. **Communicate before touching shared files**

---

## 🚀 Quick Start

### 1. Create Android Project
```
Android Studio → New Project → Empty Compose Activity
- Name: Nischint
- Package: com.nischint.app
- Min SDK: 26
- Build: Kotlin DSL
```

### 2. Add Dependencies (app/build.gradle.kts)
```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Icons
    implementation("androidx.compose.material:material-icons-extended")
}
```

### 3. Create Theme Immediately
This unblocks both frontend developers.

---

## 🎭 Demo Script (Hardcode This Path)

### The 3-Minute Golden Demo

**Scene 1: Onboarding (45 sec)**
- App opens → "Namaste! Main Nischint hoon"
- 3 quick questions with tap answers
- Shows Financial Horoscope: "₹12,000-14,500 expected expense"

**Scene 2: Home (60 sec)**
- Speedometer shows ₹2,500 safe (GREEN zone)
- Tap mic → Say "Bike" → Navigates to Goals
- Tap mic → Say "Income" → Opens Add Cash
- Show prediction card: "Next week looks stable"

**Scene 3: Tracker (45 sec)**
- Tap "Simulate SMS Sync"
- 10 transactions appear instantly
- Toggle Dhanda/Ghar → List filters
- Show auto-categorization: 🍔 Food, ⛽ Fuel

**Scene 4: Goals (30 sec)**
- Bike is 35% filled with gold color
- "3-day streak! 🔥"
- Scratch card → "You won ₹10 cashback!"

---

## ✅ Judge Checklist

| Criteria | What to Show | How |
|----------|--------------|-----|
| **Quality** | Personal feel | Akinator onboarding in Hindi |
| **Implementation** | ML Model | Prediction range on home screen |
| **Implementation** | Data Integration | SMS parsing populates tracker |
| **Impact** | Prevents debt | Safe-to-spend meter, RED when EMI due |
| **Completion** | Full app | All 3 tabs work, no crashes |
| **Business Model** | Revenue path | Scratch card rewards (affiliate), Pro badge |

---

## 🔥 Speed Tips

1. **Use MockData.kt first** - Don't wait for backend
2. **Hardcode the demo path** - Voice commands should be if-else, not real NLP
3. **Skip animations initially** - Add them in last 2 hours
4. **Test on real phone early** - Emulator lies
5. **Keep APK size small** - No heavy libraries
6. **Screenshot every working state** - Rollback insurance

---

## 📞 Emergency Contacts

If stuck:
1. Claude Code: "Fix this Compose error: [paste error]"
2. Stack Overflow: Search exact error
3. Compose docs: developer.android.com/jetpack/compose

If backend down:
1. Switch to MockData.kt entirely
2. Demo still works, just say "we have the integration ready"

If time runs out:
1. Prioritize: Home > Tracker > Goals > Onboarding
2. Home alone can win if polished enough
