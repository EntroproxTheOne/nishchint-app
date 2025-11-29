package com.nischint.app.ui.screens.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.data.preferences.AppLanguage
import com.nischint.app.data.preferences.LanguagePreferences
import com.nischint.app.data.preferences.OnboardingPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val backupEnabled: Boolean = false,
    val databaseSize: String = "Calculating...",
    val showClearDataDialog: Boolean = false,
    val showResetDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.HINDI
)

class SettingsViewModel(
    private val context: Context,
    private val database: AppDatabase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    
    private val languagePreferences = LanguagePreferences(context)
    
    init {
        loadSettings()
        loadLanguage()
        calculateDatabaseSize()
    }
    
    /**
     * Load settings from SharedPreferences
     */
    private fun loadSettings() {
        _uiState.update {
            it.copy(
                notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true),
                backupEnabled = sharedPreferences.getBoolean("backup_enabled", false)
            )
        }
    }
    
    /**
     * Load language preference
     */
    private fun loadLanguage() {
        viewModelScope.launch {
            val language = languagePreferences.getCurrentLanguage()
            _uiState.update { it.copy(currentLanguage = language) }
        }
    }
    
    /**
     * Set language preference
     */
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languagePreferences.setLanguage(language)
            _uiState.update { 
                it.copy(
                    currentLanguage = language,
                    showLanguageDialog = false
                )
            }
        }
    }
    
    /**
     * Show language selector dialog
     */
    fun showLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = true) }
    }
    
    /**
     * Hide language selector dialog
     */
    fun hideLanguageDialog() {
        _uiState.update { it.copy(showLanguageDialog = false) }
    }
    
    /**
     * Calculate database size
     */
    private fun calculateDatabaseSize() {
        viewModelScope.launch {
            try {
                val dbFile = context.getDatabasePath("nischint_database")
                val sizeInBytes = if (dbFile.exists()) dbFile.length() else 0L
                val sizeInKB = sizeInBytes / 1024.0
                val sizeInMB = sizeInKB / 1024.0
                
                val sizeString = when {
                    sizeInMB >= 1 -> String.format("%.2f MB", sizeInMB)
                    sizeInKB >= 1 -> String.format("%.2f KB", sizeInKB)
                    else -> "$sizeInBytes bytes"
                }
                
                _uiState.update { it.copy(databaseSize = sizeString) }
            } catch (e: Exception) {
                _uiState.update { it.copy(databaseSize = "Unknown") }
            }
        }
    }
    
    /**
     * Toggle notifications
     */
    fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_uiState.value.notificationsEnabled
            sharedPreferences.edit().putBoolean("notifications_enabled", newValue).apply()
            _uiState.update { it.copy(notificationsEnabled = newValue) }
        }
    }
    
    /**
     * Toggle backup
     */
    fun toggleBackup() {
        viewModelScope.launch {
            val newValue = !_uiState.value.backupEnabled
            sharedPreferences.edit().putBoolean("backup_enabled", newValue).apply()
            _uiState.update { it.copy(backupEnabled = newValue) }
        }
    }
    
    /**
     * Clear all data from database
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                // Clear user data
                database.userDao().deleteAllUsers()
                
                // Note: When TransactionEntity is added, clear transactions too
                // database.transactionDao().deleteAllTransactions()
                
                // Recalculate database size
                calculateDatabaseSize()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Export data to CSV
     */
    fun exportData() {
        viewModelScope.launch {
            try {
                // Get user data
                val user = try {
                    database.userDao().getCurrentUser().first()
                } catch (e: Exception) {
                    null
                }
                
                // Create CSV content
                val csvContent = buildString {
                    appendLine("Export Date,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
                    appendLine()
                    appendLine("User Data:")
                    appendLine("ID,Name,Phone,Language,Onboarded")
                    user?.let {
                        appendLine("${it.id},${it.name},${it.phone},${it.language},${it.isOnboarded}")
                    } ?: appendLine("No user data")
                    appendLine()
                    // Add transactions when TransactionEntity is implemented
                }
                
                // Save to file (simplified - in production, use proper file picker)
                val fileName = "nischint_export_${System.currentTimeMillis()}.csv"
                val file = File(context.getExternalFilesDir(null), fileName)
                file.writeText(csvContent)
                
                // Show success message (you can add a snackbar here)
            } catch (e: Exception) {
                // Handle error - user might not exist
            }
        }
    }
    
    /**
     * Reset app to initial state
     */
    fun resetApp() {
        viewModelScope.launch {
            try {
                // Clear database
                clearAllData()
                
                // Reset onboarding
                val onboardingPrefs = OnboardingPreferences(context)
                onboardingPrefs.setOnboardingComplete(false)
                
                // Clear all SharedPreferences
                sharedPreferences.edit().clear().apply()
                
                // Reset state
                _uiState.value = SettingsUiState()
                loadSettings()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Show clear data dialog
     */
    var showClearDataDialog: Boolean
        get() = _uiState.value.showClearDataDialog
        set(value) {
            _uiState.update { it.copy(showClearDataDialog = value) }
        }
    
    /**
     * Show reset dialog
     */
    var showResetDialog: Boolean
        get() = _uiState.value.showResetDialog
        set(value) {
            _uiState.update { it.copy(showResetDialog = value) }
        }
}

/**
 * ViewModel Factory for SettingsViewModel
 */
class SettingsViewModelFactory(
    private val context: Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                context,
                AppDatabase.getDatabase(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

