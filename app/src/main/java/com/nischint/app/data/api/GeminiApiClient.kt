package com.nischint.app.data.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Voice command actions that can be executed
 */
enum class VoiceAction {
    NAVIGATE_GOALS,
    NAVIGATE_TRACKER,
    NAVIGATE_HOME,
    ADD_INCOME,
    SHOW_HELP,
    UNKNOWN
}

/**
 * Result of voice command processing
 */
sealed class VoiceCommandResult {
    data class Success(val action: VoiceAction, val originalTranscript: String) : VoiceCommandResult()
    data class Error(val message: String, val transcript: String) : VoiceCommandResult()
}

/**
 * Client for processing voice transcripts using Gemini AI
 * 
 * Usage:
 * ```
 * val client = GeminiApiClient()
 * 
 * // Process voice transcript
 * client.processVoiceCommand("Bike dikha do").collect { result ->
 *     when (result) {
 *         is VoiceCommandResult.Success -> {
 *             // Execute action: result.action
 *         }
 *         is VoiceCommandResult.Error -> {
 *             // Handle error: result.message
 *         }
 *     }
 * }
 * ```
 */
class GeminiApiClient {
    
    companion object {
        private const val TAG = "GeminiApiClient"
    }
    
    private val generativeModel: GenerativeModel? by lazy {
        if (GeminiConfig.isConfigured()) {
            try {
                GenerativeModel(
                    modelName = GeminiConfig.MODEL_NAME,
                    apiKey = GeminiConfig.API_KEY,
                    generationConfig = generationConfig {
                        temperature = GeminiConfig.TEMPERATURE
                        maxOutputTokens = GeminiConfig.MAX_OUTPUT_TOKENS
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Gemini model", e)
                null
            }
        } else {
            Log.w(TAG, "Gemini API not configured, using fallback")
            null
        }
    }
    
    /**
     * Process voice transcript and return action
     * Uses Gemini AI if configured, otherwise falls back to pattern matching
     */
    fun processVoiceCommand(transcript: String): Flow<VoiceCommandResult> = flow {
        if (transcript.isBlank()) {
            emit(VoiceCommandResult.Error("Empty transcript", transcript))
            return@flow
        }
        
        Log.d(TAG, "Processing voice command: $transcript")
        
        // Try Gemini AI first
        if (generativeModel != null) {
            try {
                val result = processWithGemini(transcript)
                emit(result)
                return@flow
            } catch (e: Exception) {
                Log.e(TAG, "Gemini processing failed, falling back to pattern matching", e)
                // Fall through to pattern matching
            }
        }
        
        // Fallback to pattern matching
        val result = processWithPatternMatching(transcript)
        emit(result)
    }
    
    /**
     * Process using Gemini AI
     */
    private suspend fun processWithGemini(transcript: String): VoiceCommandResult {
        return try {
            val prompt = "${GeminiConfig.VOICE_COMMAND_PROMPT}\n\nUser said: \"$transcript\"\n\nAction:"
            
            val response = generativeModel?.generateContent(prompt)
            val actionText = response?.text?.trim()?.lowercase() ?: ""
            
            Log.d(TAG, "Gemini response: $actionText")
            
            val action = when {
                actionText.contains("navigate_goals") -> VoiceAction.NAVIGATE_GOALS
                actionText.contains("navigate_tracker") -> VoiceAction.NAVIGATE_TRACKER
                actionText.contains("navigate_home") -> VoiceAction.NAVIGATE_HOME
                actionText.contains("add_income") -> VoiceAction.ADD_INCOME
                actionText.contains("show_help") -> VoiceAction.SHOW_HELP
                else -> {
                    // If Gemini gives unclear response, use pattern matching
                    Log.w(TAG, "Unclear Gemini response, using pattern matching")
                    return processWithPatternMatching(transcript)
                }
            }
            
            VoiceCommandResult.Success(action, transcript)
            
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API error", e)
            throw e
        }
    }
    
    /**
     * Fallback pattern matching for voice commands
     * Works without Gemini API
     */
    private fun processWithPatternMatching(transcript: String): VoiceCommandResult {
        val normalized = transcript.lowercase().trim()
        
        Log.d(TAG, "Using pattern matching for: $normalized")
        
        val action = when {
            // Goal/Bike related
            normalized.contains("bike") ||
            normalized.contains("goal") ||
            normalized.contains("saving") ||
            normalized.contains("save") ||
            normalized.contains("bachao") ||
            normalized.contains("bacha") -> VoiceAction.NAVIGATE_GOALS
            
            // Tracker/Expense related
            normalized.contains("tracker") ||
            normalized.contains("track") ||
            normalized.contains("expense") ||
            normalized.contains("kharcha") ||
            normalized.contains("kharch") ||
            normalized.contains("transaction") ||
            normalized.contains("spend") ||
            normalized.contains("spent") -> VoiceAction.NAVIGATE_TRACKER
            
            // Income/Add money related
            normalized.contains("income") ||
            normalized.contains("add") ||
            normalized.contains("cash") ||
            normalized.contains("paisa") ||
            normalized.contains("paise") ||
            normalized.contains("money") ||
            normalized.contains("tip") -> VoiceAction.ADD_INCOME
            
            // Home related
            normalized.contains("home") ||
            normalized.contains("ghar") ||
            normalized.contains("dashboard") ||
            normalized.contains("main") -> VoiceAction.NAVIGATE_HOME
            
            // Help related
            normalized.contains("help") ||
            normalized.contains("madad") ||
            normalized.contains("support") -> VoiceAction.SHOW_HELP
            
            // Unknown
            else -> VoiceAction.UNKNOWN
        }
        
        return if (action != VoiceAction.UNKNOWN) {
            VoiceCommandResult.Success(action, transcript)
        } else {
            VoiceCommandResult.Error("Command not recognized", transcript)
        }
    }
    
    /**
     * Check if Gemini AI is available
     */
    fun isGeminiAvailable(): Boolean {
        return generativeModel != null
    }
    
    /**
     * Get suggestion text for voice commands
     */
    fun getVoiceCommandSuggestions(): List<String> {
        return listOf(
            "Bike",
            "Tracker",
            "Income",
            "Home",
            "Goal dikha",
            "Kharcha dekho",
            "Paisa add karo"
        )
    }
}

/**
 * Extension function to convert VoiceAction to human-readable description
 */
fun VoiceAction.toDescription(): String {
    return when (this) {
        VoiceAction.NAVIGATE_GOALS -> "Opening Goals"
        VoiceAction.NAVIGATE_TRACKER -> "Opening Tracker"
        VoiceAction.NAVIGATE_HOME -> "Going Home"
        VoiceAction.ADD_INCOME -> "Adding Income"
        VoiceAction.SHOW_HELP -> "Showing Help"
        VoiceAction.UNKNOWN -> "Unknown Command"
    }
}

/**
 * Extension function to convert VoiceAction to icon emoji
 */
fun VoiceAction.toIcon(): String {
    return when (this) {
        VoiceAction.NAVIGATE_GOALS -> "🎯"
        VoiceAction.NAVIGATE_TRACKER -> "📊"
        VoiceAction.NAVIGATE_HOME -> "🏠"
        VoiceAction.ADD_INCOME -> "💰"
        VoiceAction.SHOW_HELP -> "❓"
        VoiceAction.UNKNOWN -> "🤷"
    }
}
