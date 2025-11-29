package com.nischint.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.nischint.app.data.preferences.AppLanguage
import com.nischint.app.data.preferences.LanguagePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Local composition for current language
 */
val LocalLanguage = compositionLocalOf<AppLanguage> { AppLanguage.HINDI }

/**
 * Language-aware string resources
 */
object Strings {
    // Common
    object Common {
        fun save(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "सहेजें"
            AppLanguage.ENGLISH -> "Save"
        }
        
        fun cancel(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "रद्द करें"
            AppLanguage.ENGLISH -> "Cancel"
        }
        
        fun delete(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "हटाएं"
            AppLanguage.ENGLISH -> "Delete"
        }
        
        fun edit(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "संपादित करें"
            AppLanguage.ENGLISH -> "Edit"
        }
        
        fun done(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "पूर्ण"
            AppLanguage.ENGLISH -> "Done"
        }
        
        fun loading(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "लोड हो रहा है..."
            AppLanguage.ENGLISH -> "Loading..."
        }
    }
    
    // Home Screen
    object Home {
        fun welcome(lang: AppLanguage, name: String) = when (lang) {
            AppLanguage.HINDI -> "नमस्ते, $name!"
            AppLanguage.ENGLISH -> "Hello, $name!"
        }
        
        fun financialHealth(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "वित्तीय स्वास्थ्य"
            AppLanguage.ENGLISH -> "Financial Health"
        }
        
        fun prediction(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "भविष्यवाणी"
            AppLanguage.ENGLISH -> "Prediction"
        }
        
        fun goals(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "लक्ष्य"
            AppLanguage.ENGLISH -> "Goals"
        }
        
        fun addCash(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "नकदी जोड़ें"
            AppLanguage.ENGLISH -> "Add Cash"
        }
        
        fun tapToSpeak(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "बोलने के लिए टैप करें"
            AppLanguage.ENGLISH -> "Tap to Speak"
        }
    }
    
    // Tracker Screen
    object Tracker {
        fun title(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "ट्रैकर"
            AppLanguage.ENGLISH -> "Tracker"
        }
        
        fun parseSms(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "SMS Parse करो"
            AppLanguage.ENGLISH -> "Parse SMS"
        }
        
        fun parseScreenshot(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "Screenshot से Transaction Add करो"
            AppLanguage.ENGLISH -> "Add Transaction from Screenshot"
        }
        
        fun transactions(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "लेनदेन"
            AppLanguage.ENGLISH -> "Transactions"
        }
        
        fun noTransactions(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "कोई लेनदेन नहीं"
            AppLanguage.ENGLISH -> "No transactions"
        }
    }
    
    // Goals Screen
    object Goals {
        fun title(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "लक्ष्य"
            AppLanguage.ENGLISH -> "Goals"
        }
        
        fun addGoal(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "लक्ष्य जोड़ें"
            AppLanguage.ENGLISH -> "Add Goal"
        }
        
        fun noGoals(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "कोई लक्ष्य नहीं"
            AppLanguage.ENGLISH -> "No goals"
        }
    }
    
    // Settings Screen
    object Settings {
        fun title(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "सेटिंग्स"
            AppLanguage.ENGLISH -> "Settings"
        }
        
        fun language(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "भाषा"
            AppLanguage.ENGLISH -> "Language"
        }
        
        fun selectLanguage(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "भाषा चुनें"
            AppLanguage.ENGLISH -> "Select Language"
        }
        
        fun profile(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "प्रोफ़ाइल"
            AppLanguage.ENGLISH -> "Profile"
        }
        
        fun notifications(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "सूचनाएं"
            AppLanguage.ENGLISH -> "Notifications"
        }
        
        fun enabled(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "सक्षम"
            AppLanguage.ENGLISH -> "Enabled"
        }
        
        fun disabled(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "अक्षम"
            AppLanguage.ENGLISH -> "Disabled"
        }
    }
    
    // Onboarding
    object Onboarding {
        fun enterName(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "अपना नाम दर्ज करें"
            AppLanguage.ENGLISH -> "Enter your name"
        }
        
        fun continueText(lang: AppLanguage) = when (lang) {
            AppLanguage.HINDI -> "जारी रखें"
            AppLanguage.ENGLISH -> "Continue"
        }
    }
}

/**
 * Composable to provide language context
 */
@Composable
fun ProvideLanguage(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLanguage provides language) {
        content()
    }
}

/**
 * Get current language from context
 */
@Composable
fun rememberCurrentLanguage(): AppLanguage {
    val context = LocalContext.current
    val languagePrefs = remember { LanguagePreferences(context) }
    val language by languagePrefs.currentLanguage.collectAsState(initial = AppLanguage.HINDI)
    return language
}

