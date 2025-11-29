package com.nischint.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore for onboarding preferences
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

/**
 * Keys for onboarding preferences
 */
private object OnboardingKeys {
    val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
}

/**
 * Repository for managing onboarding state
 */
class OnboardingPreferences(private val context: Context) {
    
    /**
     * Flow of onboarding completion status
     */
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[OnboardingKeys.IS_ONBOARDING_COMPLETE] ?: false
        }
    
    /**
     * Mark onboarding as complete
     */
    suspend fun setOnboardingComplete(complete: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[OnboardingKeys.IS_ONBOARDING_COMPLETE] = complete
        }
    }
    
    /**
     * Check if onboarding is complete (synchronous, for initial check)
     * Note: This reads from DataStore synchronously - use sparingly
     */
    suspend fun getOnboardingComplete(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[OnboardingKeys.IS_ONBOARDING_COMPLETE] ?: false
        }.first()
    }
}

