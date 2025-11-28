# Nischint - Quick Start Guide

## 🚀 Get Running in 10 Minutes

### 1. Install Android Studio (5 min)
- Download: https://developer.android.com/studio
- Install with default options
- ✅ Done when you see welcome screen

### 2. Open Project (2 min)
1. Launch Android Studio
2. Click **"Open"**
3. Navigate to **this folder** (`App/`)
4. Click **"OK"**
5. Wait for "Gradle sync" to finish (status bar at bottom)

### 3. Run the App (3 min)

#### Option A: On Emulator (Easiest)
1. Click **Tools → Device Manager**
2. Click **"Create Device"** if you don't have one
3. Choose **Pixel 5** with **API 34**
4. Click green **play button (▶)** in toolbar
5. ✅ App launches!

#### Option B: On Your Phone
1. Enable **USB Debugging** on your phone:
   - Settings → About Phone
   - Tap "Build Number" 7 times
   - Settings → Developer Options → USB Debugging (ON)
2. Connect USB cable
3. Click green **play button (▶)**
4. ✅ App installs and launches!

---

## 📱 What You'll See

✅ **Home Screen** - Speedometer showing ₹2,500 safe to spend
✅ **Bottom Nav** - Home, Tracker, Goals buttons
✅ **Mic Button** - Floating gold button in center
✅ **Voice Dialog** - Tap mic → Demo voice commands
✅ **Tracker** - Transaction list with "Simulate SMS" button
✅ **Goals** - Bike with 35% progress filled

---

## 🎯 Test Checklist (1 min)

- [ ] App opens without crash
- [ ] Tap "Tracker" → See transaction list
- [ ] Tap "Goals" → See bike progress
- [ ] Tap mic button → Dialog opens
- [ ] Tap "Bike" in dialog → Navigates to Goals
- [ ] Tap "Home" → Back to home screen

**✅ All working? You're ready to demo!**

---

## 🔧 If Something Breaks

### "SDK not found"
→ File → Project Structure → SDK Location → Set Android SDK path

### "Gradle sync failed"
→ File → Invalidate Caches → Invalidate and Restart

### "App crashes"
→ Check Logcat (bottom panel) for red errors

### "Missing icons"
→ Ignore for now, app works with defaults

---

## 📦 Build APK for Demo

1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for notification
3. Click "locate"
4. APK at: `app/build/outputs/apk/debug/app-debug.apk`
5. Transfer to phone and install

---

## 🎨 Current Features

**Working Now (Mock Data):**
- ✅ All 4 screens
- ✅ Navigation
- ✅ Neomorphic design
- ✅ Speedometer gauge
- ✅ Voice dialog (demo buttons)
- ✅ Transaction list
- ✅ Goals progress

**Connect Later:**
- ⏳ Backend API (update `BASE_URL` in `ApiClient.kt`)
- ⏳ Real voice recognition
- ⏳ SMS parsing
- ⏳ ML predictions

---

## 📚 Full Docs

- **Detailed Instructions:** `BUILD_INSTRUCTIONS.md`
- **Project Overview:** `PROJECT_STATUS.md`
- **Architecture:** `ARCHITECTURE.md`
- **Design Guide:** `CLAUDE.md`

---

## 💡 Pro Tips

1. **Demo Path:** Home → Tap Mic → Say "Bike" → Goals screen
2. **Fallback:** If voice fails, use bottom nav
3. **Mock Data:** Edit `MockData.kt` to change amounts/transactions
4. **Backend:** App works standalone, connect later

---

**🎉 You're ready! The app is built and works. Open in Android Studio and run it!**
