# Gemini API Setup Guide

## 🔑 Getting Your Gemini API Key

### Step 1: Get API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Get API Key" or "Create API Key"
4. Copy the generated API key

### Step 2: Add API Key to App

**Option A: For Development/Demo (Quick)**

Open this file:
```
app/src/main/java/com/nischint/app/data/api/GeminiConfig.kt
```

Replace this line:
```kotlin
const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"
```

With your actual key:
```kotlin
const val API_KEY = "AIzaSyABC123XYZ456..."  // Your real key
```

**Option B: For Production (Secure)**

1. Add to `local.properties` (this file is gitignored):
```properties
GEMINI_API_KEY=AIzaSyABC123XYZ456...
```

2. Update `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        // Read from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
}
```

3. Update `GeminiConfig.kt`:
```kotlin
const val API_KEY = BuildConfig.GEMINI_API_KEY
```

### Step 3: Verify Setup

Run the app and try the voice command feature. If configured correctly, you should see voice commands being processed.

---

## 🎤 How Voice Commands Work

1. **User taps mic button** → Opens voice dialog
2. **User speaks** → Android SpeechRecognizer captures audio
3. **Speech to text** → Converts voice to text transcript
4. **Gemini AI processes** → Classifies intent (e.g., "bike" → navigate to goals)
5. **App executes** → Performs the action

---

## 🔒 Security Best Practices

### ⚠️ DO NOT:
- ❌ Commit API keys to Git
- ❌ Share API keys publicly
- ❌ Hardcode keys in production apps

### ✅ DO:
- ✅ Use `local.properties` (gitignored)
- ✅ Use environment variables in CI/CD
- ✅ Use BuildConfig for production
- ✅ Rotate keys if exposed
- ✅ Set usage limits in Google Cloud Console

---

## 📊 API Usage & Limits

**Free Tier (as of 2024):**
- 60 requests per minute
- Gemini Pro model included
- Sufficient for demo and small apps

**Monitor Usage:**
- [Google AI Studio Dashboard](https://makersuite.google.com/)
- Check quotas and billing

---

## 🐛 Troubleshooting

### "API Key not configured"
- Check if `GeminiConfig.isConfigured()` returns true
- Verify key is not "YOUR_GEMINI_API_KEY_HERE"
- Rebuild project after adding key

### "API Error: 403"
- API key is invalid
- Generate a new key from Google AI Studio

### "Network Error"
- Check internet connection
- Verify app has INTERNET permission

### "No response from Gemini"
- Check API quota not exceeded
- Verify model name is correct ("gemini-pro")
- Check logs for detailed error messages

---

## 🔧 Testing Without API Key

If you don't have a Gemini API key yet, you can still test the app:

1. The voice recognition will work (speech-to-text)
2. Simple pattern matching will be used as fallback:
   - "bike" → Goals screen
   - "tracker" → Tracker screen
   - "home" → Home screen
   - "income" → Add cash dialog

For full AI-powered voice commands, add the Gemini API key.

---

## 📚 Additional Resources

- [Gemini API Documentation](https://ai.google.dev/docs)
- [Google AI Studio](https://makersuite.google.com/)
- [Gemini API Pricing](https://ai.google.dev/pricing)
- [API Key Best Practices](https://cloud.google.com/docs/authentication/api-keys)

---

**Ready to go!** Add your API key and start using voice commands! 🎤✨

