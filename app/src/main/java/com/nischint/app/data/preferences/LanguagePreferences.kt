package com.nischint.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore for language preferences
 */
private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs")

/**
 * Keys for language preferences
 */
private object LanguageKeys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
}

/**
 * Supported languages
 */
enum class AppLanguage(val code: String, val displayName: String) {
    HINDI("hi", "हिंदी"),
    ENGLISH("en", "English");
    
    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code == code } ?: HINDI
        }
    }
}

/**
 * Repository for managing language preferences
 */
class LanguagePreferences(private val context: Context) {
    
    /**
     * Flow of current language
     */
    val currentLanguage: Flow<AppLanguage> = context.languageDataStore.data
        .map { preferences ->
            val code = preferences[LanguageKeys.LANGUAGE_CODE] ?: "hi"
            AppLanguage.fromCode(code)
        }
    
    /**
     * Set language preference
     */
    suspend fun setLanguage(language: AppLanguage) {
        context.languageDataStore.edit { preferences ->
            preferences[LanguageKeys.LANGUAGE_CODE] = language.code
        }
    }
    
    /**
     * Get current language (synchronous, for initial check)
     */
    suspend fun getCurrentLanguage(): AppLanguage {
        return context.languageDataStore.data.map { preferences ->
            val code = preferences[LanguageKeys.LANGUAGE_CODE] ?: "hi"
            AppLanguage.fromCode(code)
        }.first()
    }
}

