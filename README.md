# Nischint - Financial Management App for Delivery Workers

<div align="center">

![Nischint Logo](icon.png)

**A smart financial management app designed specifically for delivery workers in India**

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/android-API%2026%2B-green.svg)](https://developer.android.com)

</div>

---

## 📱 About

**Nischint** is a comprehensive financial management Android application built for delivery workers (Zomato, Swiggy, Amazon, Flipkart) in India. The app helps users track expenses, manage savings goals, analyze spending patterns, and make informed financial decisions.

### Key Features

- 🎯 **Smart Financial Tracking** - Track income and expenses with automatic SMS parsing
- 📊 **AI-Powered Insights** - Get personalized financial analysis using Google Gemini AI
- 🎨 **Modern UI/UX** - Beautiful dark theme with yellow accents and glassmorphism effects
- 🗣️ **Voice Commands** - Hindi/Hinglish voice input for hands-free operation
- 📸 **Screenshot Parsing** - Extract transaction data from payment screenshots
- 🎯 **Goal Management** - Set and track savings goals (bike, emergency fund, etc.)
- 🌐 **Bilingual Support** - Full Hindi and English language support
- 📱 **Offline-First** - Local database for reliable data storage
- 🔐 **Privacy-Focused** - All data stored locally on device

---

## 🚀 Features

### Core Functionality

- **Transaction Management**
  - Manual transaction entry
  - Automatic SMS parsing from bank/payment apps
  - Screenshot-based transaction extraction
  - Categorization (Food, Fuel, Grocery, EMI, etc.)
  - Business vs Personal expense tracking

- **Financial Analysis**
  - AI-powered spending pattern analysis
  - Risk assessment (Low/Medium/High)
  - Savings potential calculation
  - Personalized recommendations
  - Monthly expense predictions

- **Goal Tracking**
  - Create custom savings goals
  - Track progress towards goals
  - Visual progress indicators
  - Goal completion notifications

- **Onboarding Experience**
  - Personalized Hindi/Hinglish questions
  - AI-generated conversational flow
  - Character-by-character typing animation
  - Horoscope-based financial predictions

### Technical Features

- **Modern Architecture**
  - MVVM (Model-View-ViewModel) pattern
  - Jetpack Compose for UI
  - Room Database for local storage
  - DataStore for preferences
  - Coroutines & Flow for async operations

- **AI Integration**
  - Google Gemini 2.0 Flash for:
    - Dynamic onboarding questions
    - Financial analysis and insights
    - Voice command understanding
    - Speech-to-text enhancement

- **UI/UX**
  - Dark gray and yellow theme
  - Glassmorphism effects
  - Smooth animations and transitions
  - Haptic feedback
  - Gradient backgrounds
  - Custom speedometer gauge

---

## 📋 Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17 or higher
- **Android SDK** API 26+ (Android 8.0+)
- **Gradle** 8.9+
- **Google Gemini API Key** (for AI features)

---

## 🛠️ Installation

### 1. Clone the Repository

```bash
git clone https://github.com/EntroproxTheOne/nishchint-app.git
cd nishchint-app
```

### 2. Setup Gemini API Key

1. Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Open `app/src/main/java/com/nischint/app/data/api/GeminiConfig.kt`
3. Replace `API_KEY` with your actual key:

```kotlin
const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"
```

> ⚠️ **Security Note**: For production, use environment variables or BuildConfig instead of hardcoding the API key.

### 3. Open in Android Studio

1. Open Android Studio
2. Select **File → Open**
3. Navigate to the cloned repository folder
4. Click **OK**
5. Wait for Gradle sync to complete (may take 5-10 minutes on first run)

### 4. Run the App

1. Connect an Android device (API 26+) or start an emulator
2. Click the **Run** button (▶) or press `Shift + F10`
3. The app will build and install automatically

---

## 📁 Project Structure

```
nishchint-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/nischint/app/
│   │   │   ├── MainActivity.kt              # App entry point
│   │   │   ├── navigation/                  # Navigation components
│   │   │   ├── ui/
│   │   │   │   ├── screens/                 # Screen composables
│   │   │   │   │   ├── home/               # Home screen
│   │   │   │   │   ├── tracker/            # Transaction tracker
│   │   │   │   │   ├── goals/               # Goals management
│   │   │   │   │   ├── onboarding/          # Onboarding flow
│   │   │   │   │   ├── profile/             # User profile
│   │   │   │   │   ├── settings/            # App settings
│   │   │   │   │   └── splash/             # Splash screen
│   │   │   │   ├── components/              # Reusable UI components
│   │   │   │   ├── theme/                   # Theme & colors
│   │   │   │   └── utils/                   # UI utilities
│   │   │   ├── data/
│   │   │   │   ├── api/                     # API clients & services
│   │   │   │   ├── database/                # Room database entities
│   │   │   │   ├── models/                  # Data models
│   │   │   │   └── preferences/             # DataStore preferences
│   │   │   └── utils/                       # Utility classes
│   │   └── res/                             # Resources (drawables, values)
│   └── build.gradle.kts                     # App-level build config
├── gradle/
│   └── libs.versions.toml                   # Dependency versions
├── build.gradle.kts                         # Project-level build config
└── README.md                                # This file
```

---

## 🎨 Screenshots

### Home Screen
- Financial health speedometer
- AI-powered insights card
- Quick access to goals and predictions

### Tracker Screen
- Transaction list with categories
- SMS parsing functionality
- Screenshot transaction extraction

### Goals Screen
- Savings goals with progress tracking
- Visual progress indicators

### Settings Screen
- Language selection (Hindi/English)
- Database management
- Privacy & security options

---

## 🔧 Configuration

### Permissions

The app requires the following permissions:

- `RECORD_AUDIO` - For voice input
- `READ_SMS` - For automatic transaction parsing
- `READ_MEDIA_IMAGES` - For screenshot parsing
- `CAMERA` - For capturing screenshots
- `INTERNET` - For Gemini API calls
- `VIBRATE` - For haptic feedback

### Build Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin**: 1.9.0
- **Gradle**: 8.9

---

## 🧪 Testing

### Running Tests

```bash
./gradlew test
```

### Building APK

```bash
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📚 Documentation

- [Architecture Overview](ARCHITECTURE.md) - Technical architecture details
- [Project Status](PROJECT_STATUS.md) - Current implementation status
- [Build Instructions](BUILD_INSTRUCTIONS.md) - Detailed build guide
- [Gemini API Setup](GEMINI_API_SETUP.md) - AI integration guide
- [Quick Start Guide](QUICKSTART.md) - Quick setup guide

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

---

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Google Gemini AI** - For AI-powered features
- **Jetpack Compose** - For modern UI development
- **Material Design 3** - For design system
- **Room Database** - For local data persistence

---

## 📞 Support

For issues, questions, or contributions:

- **GitHub Issues**: [Create an issue](https://github.com/EntroproxTheOne/nishchint-app/issues)
- **Repository**: [nishchint-app](https://github.com/EntroproxTheOne/nishchint-app)

---

## 🗺️ Roadmap

- [ ] Goals functionality implementation
- [ ] Enhanced SMS parsing with more bank formats
- [ ] PyTorch LLM integration for screenshot parsing
- [ ] Export data to CSV/PDF
- [ ] Cloud backup and sync
- [ ] Multi-language support expansion
- [ ] Widget support
- [ ] Wear OS companion app

---

## ⚠️ Known Issues

- Screenshot parsing requires PyTorch model (placeholder implemented)
- Some bank SMS formats may not be recognized
- Goals feature is currently UI-only (database integration pending)

---

## 🎯 Future Enhancements

- **Advanced Analytics**
  - Spending trends over time
  - Category-wise breakdown charts
  - Monthly/yearly comparisons

- **Social Features**
  - Share financial goals
  - Compare with peers (anonymized)
  - Community tips and advice

- **Integration**
  - UPI payment integration
  - Bank account linking
  - Credit score tracking

---

<div align="center">

**Made with ❤️ for delivery workers in India**

[⭐ Star this repo](https://github.com/EntroproxTheOne/nishchint-app) if you find it helpful!

BUILT FOR A HACKATHON - MUMBAI-HACKS

</div>

