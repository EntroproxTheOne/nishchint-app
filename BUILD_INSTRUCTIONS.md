# Nischint Android App - Build Instructions

## Prerequisites

1. **Android Studio** (Latest version recommended)
   - Download from: https://developer.android.com/studio
   - Make sure to install Android SDK during setup

2. **Java Development Kit (JDK) 11 or higher**
   - Usually bundled with Android Studio

3. **Minimum Android SDK Requirements:**
   - Min SDK: 26 (Android 8.0)
   - Target SDK: 35 (Android 14)
   - Compile SDK: 35

## Project Structure

The project follows standard Android app architecture:

```
Nischint/
├── app/
│   ├── src/main/
│   │   ├── java/com/nischint/app/
│   │   │   ├── MainActivity.kt           # App entry point
│   │   │   ├── navigation/               # Navigation setup
│   │   │   ├── ui/
│   │   │   │   ├── theme/               # Design system (colors, typography)
│   │   │   │   ├── components/          # Reusable UI components
│   │   │   │   └── screens/             # All app screens
│   │   │   └── data/
│   │   │       ├── api/                 # API client, mock data
│   │   │       └── models/              # Data classes
│   │   ├── res/                         # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml               # Dependency versions
│   └── wrapper/
├── build.gradle.kts                     # Root build file
├── settings.gradle.kts                  # Project settings
└── gradle.properties                    # Gradle configuration
```

## Step 1: Open Project in Android Studio

1. Launch **Android Studio**
2. Click **"Open"** (not "New Project")
3. Navigate to the `App` folder (the one containing this file)
4. Click **"OK"**

Android Studio will:
- Detect it as a Gradle project
- Download necessary Gradle wrapper files
- Sync Gradle dependencies (this may take a few minutes)

## Step 2: Wait for Gradle Sync

You'll see a progress bar at the bottom:
- "Gradle sync in progress..."
- This downloads all dependencies (Compose, Retrofit, etc.)
- **First-time sync can take 5-10 minutes**

If you encounter errors:
- Click **"File" → "Sync Project with Gradle Files"**
- Or click the elephant icon (🐘) in the toolbar

## Step 3: Add Launcher Icons (Important!)

The app needs launcher icons. You have two options:

### Option A: Use Android Studio's Image Asset Studio (Recommended)
1. Right-click on `app/src/main/res`
2. Select **"New" → "Image Asset"**
3. Choose **"Launcher Icons (Adaptive and Legacy)"**
4. Upload your logo/icon or use a clipart
5. Click **"Next"** and **"Finish"**

### Option B: Manual (Quick Fix)
- Copy any PNG icon files to:
  - `app/src/main/res/mipmap-mdpi/ic_launcher.png` (48x48)
  - `app/src/main/res/mipmap-hdpi/ic_launcher.png` (72x72)
  - `app/src/main/res/mipmap-xhdpi/ic_launcher.png` (96x96)
  - `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` (144x144)
  - `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` (192x192)

**For now**, you can skip this - Android Studio will use default icons.

## Step 4: Run on Emulator

### Create an Emulator (if you don't have one):
1. Click **"Tools" → "Device Manager"**
2. Click **"Create Device"**
3. Choose **"Pixel 5"** or any phone with **API 26+**
4. Select **API Level 34** (or higher)
5. Click **"Next"** and **"Finish"**

### Run the App:
1. Click the **green play button (▶)** in the toolbar
2. Select your emulator from the dropdown
3. Click **"OK"**

The app will:
- Build the project
- Install on the emulator
- Launch automatically

**Expected behavior:**
- App opens to the **Home Screen** with a speedometer gauge
- Bottom navigation shows: Home, Tracker, Goals
- Floating mic button in the center
- All UI should be visible (currently with mock data)

## Step 5: Run on Physical Device

1. **Enable Developer Options** on your Android phone:
   - Go to **Settings → About Phone**
   - Tap **"Build Number"** 7 times
   - Go back to Settings → **"Developer Options"**
   - Enable **"USB Debugging"**

2. **Connect phone via USB cable**

3. In Android Studio:
   - Your phone will appear in the device dropdown
   - Click **"Run"** (green play button)
   - On your phone, allow USB debugging when prompted

## Step 6: Generate APK

To create an installable APK:

1. Click **"Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"**
2. Wait for the build to complete (you'll see a notification)
3. Click **"locate"** in the notification
4. The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

**To install the APK:**
- Transfer to your phone
- Open and install (you may need to allow "Install from Unknown Sources")

## Troubleshooting

### Issue: "SDK not found"
- **Solution:** Go to **"File" → "Project Structure" → "SDK Location"**
- Set Android SDK path (usually `C:\Users\YourName\AppData\Local\Android\Sdk` on Windows)

### Issue: "Gradle sync failed"
- **Solution 1:** Check internet connection (Gradle downloads dependencies)
- **Solution 2:** Click **"File" → "Invalidate Caches" → "Invalidate and Restart"**
- **Solution 3:** Delete `.gradle` folder in project and sync again

### Issue: "Compose compiler version mismatch"
- **Solution:** Make sure `gradle/libs.versions.toml` has matching versions:
  ```toml
  kotlin = "1.9.22"
  compose = "1.9.22"
  ```

### Issue: "App crashes on launch"
- **Solution 1:** Check Logcat (bottom panel) for error messages
- **Solution 2:** Make sure minimum SDK is 26 or higher
- **Solution 3:** Clean and rebuild: **"Build" → "Clean Project"** then **"Build" → "Rebuild Project"**

### Issue: Missing launcher icons
- See **Step 3** above
- Temporarily, the app will use default Android icons

## Development Workflow

### Making Changes:
1. Edit Kotlin files in `app/src/main/java/com/nischint/app/`
2. Android Studio auto-compiles on save
3. Click **"Run"** to see changes on emulator/device

### Hot Reload (for Compose UI):
- Some changes update automatically
- For full changes, click **"Run"** again

### Viewing Logs:
- Open **"Logcat"** tab (bottom panel)
- Filter by package: `com.nischint.app`
- Look for errors in red

## Current App Status

✅ **Working:**
- Complete Android project structure
- All 4 screens implemented (Home, Tracker, Goals, Onboarding)
- Navigation with bottom nav bar
- Neomorphic design system
- Mock data integration
- API client setup (Retrofit)
- Voice command placeholders

⚠️ **Not Yet Connected:**
- Real backend API (currently using mock data)
- Actual voice recognition (demo buttons work)
- SMS parsing integration
- ML model prediction (placeholder data)

⏭️ **Next Steps:**
1. Add launcher icons (Step 3)
2. Run and verify the app works
3. Connect to FastAPI backend when ready
4. Test with real data

## Testing Checklist

Run through this before demo:

- [ ] App launches without crash
- [ ] Home screen shows speedometer
- [ ] Bottom navigation works (tap all 3 tabs)
- [ ] Mic button opens voice dialog
- [ ] Voice buttons navigate correctly
- [ ] Tracker shows transaction list
- [ ] Goals shows bike with progress
- [ ] No visual glitches or broken layouts

## Backend Integration (When Ready)

1. Update `ApiConfig` in `app/src/main/java/com/nischint/app/data/api/ApiClient.kt`:
   ```kotlin
   const val BASE_URL = "https://your-ngrok-url.ngrok.io"
   ```

2. Run FastAPI backend locally:
   ```bash
   cd backend
   uvicorn app.main:app --reload
   ```

3. Use ngrok for phone testing:
   ```bash
   ngrok http 8000
   ```

4. Copy ngrok URL to `BASE_URL`

## Performance Notes

- **First build:** 3-5 minutes (downloads dependencies)
- **Subsequent builds:** 10-30 seconds
- **APK size:** ~8-15 MB (debug build)

## Support

If you encounter issues not covered here:
1. Check Logcat for error messages
2. Google the exact error message
3. Check Stack Overflow
4. Android Developer Docs: https://developer.android.com

---

**Good luck with Mumbai Hacks! 🚀**

The app is ready to build and run. Focus on making the demo smooth and connecting the backend when ready.
