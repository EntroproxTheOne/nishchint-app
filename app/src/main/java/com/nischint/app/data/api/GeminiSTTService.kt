package com.nischint.app.data.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Gemini Speech-to-Text Service
 * Uses Gemini API for enhanced Hindi/Hinglish speech recognition
 * 
 * Note: Gemini doesn't have direct STT API, but we can use it to:
 * 1. Enhance Android SpeechRecognizer transcripts (post-processing)
 * 2. Improve accuracy for Hindi/Hinglish commands
 * 3. Handle context-aware transcript correction
 */
class GeminiSTTService {
    
    companion object {
        private const val TAG = "GeminiSTTService"
        
        private const val TRANSCRIPT_ENHANCEMENT_PROMPT = """
You are a speech-to-text enhancer for a financial app in India.
The user speaks in Hindi/Hinglish (mix of Hindi and English).

Your job is to:
1. Correct common speech recognition errors
2. Normalize Hindi/Hinglish text
3. Extract the intended command/action

Common commands users might say:
- "Bike dikha" / "Bike dikhao" → "bike"
- "Goal dikha" / "Goal dikhao" → "goals"
- "Kharcha dikha" / "Expense dikha" → "tracker"
- "Transaction dikha" → "tracker"
- "Paisa add karo" / "Income add karo" → "income"
- "Cash add karo" → "income"
- "Home" / "Ghar" → "home"
- "Tracker" / "Kharcha" → "tracker"

Input: Raw transcript from speech recognizer (may have errors)
Output: Cleaned, normalized command word (one word only)

Examples:
Input: "bike dikha do" → Output: "bike"
Input: "goal dikhao" → Output: "goals"
Input: "kharcha dikha" → Output: "tracker"
Input: "paisa add karo" → Output: "income"
Input: "transaction dikha" → Output: "tracker"

Return ONLY the command word, nothing else.
"""
    }
    
    private val generativeModel: GenerativeModel? by lazy {
        if (GeminiConfig.isConfigured()) {
            try {
                GenerativeModel(
                    modelName = GeminiConfig.MODEL_NAME,
                    apiKey = GeminiConfig.API_KEY,
                    generationConfig = generationConfig {
                        temperature = 0.3f  // Lower temperature for more accurate corrections
                        maxOutputTokens = 32  // Short responses only
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Gemini model", e)
                null
            }
        } else {
            Log.w(TAG, "Gemini API not configured")
            null
        }
    }
    
    /**
     * Enhance speech transcript using Gemini AI
     * Improves accuracy for Hindi/Hinglish commands
     */
    fun enhanceTranscript(rawTranscript: String): Flow<String> = flow {
        if (rawTranscript.isBlank()) {
            emit("")
            return@flow
        }
        
        Log.d(TAG, "Enhancing transcript: $rawTranscript")
        
        // If Gemini not available, return original transcript
        if (generativeModel == null) {
            emit(normalizeTranscript(rawTranscript))
            return@flow
        }
        
        try {
            val prompt = "$TRANSCRIPT_ENHANCEMENT_PROMPT\n\nInput: \"$rawTranscript\"\n\nOutput:"
            
            val response = generativeModel?.generateContent(prompt)
            val enhanced = response?.text?.trim()?.lowercase() ?: ""
            
            Log.d(TAG, "Enhanced transcript: $enhanced")
            
            if (enhanced.isNotEmpty()) {
                emit(enhanced)
            } else {
                emit(normalizeTranscript(rawTranscript))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error enhancing transcript with Gemini", e)
            emit(normalizeTranscript(rawTranscript))
        }
    }
    
    /**
     * Normalize transcript without Gemini (fallback)
     * Handles common Hindi/Hinglish patterns
     */
    private fun normalizeTranscript(transcript: String): String {
        val normalized = transcript.lowercase().trim()
        
        // Common Hindi/Hinglish patterns
        return when {
            // Bike/Goal related
            normalized.contains("bike") || 
            normalized.contains("goal") || 
            normalized.contains("saving") ||
            normalized.contains("bachao") ||
            normalized.contains("bacha") -> "bike"
            
            // Tracker/Expense related
            normalized.contains("tracker") ||
            normalized.contains("kharcha") ||
            normalized.contains("kharch") ||
            normalized.contains("expense") ||
            normalized.contains("transaction") ||
            normalized.contains("spend") -> "tracker"
            
            // Income/Add money related
            normalized.contains("income") ||
            normalized.contains("add") ||
            normalized.contains("cash") ||
            normalized.contains("paisa") ||
            normalized.contains("paise") ||
            normalized.contains("money") -> "income"
            
            // Home related
            normalized.contains("home") ||
            normalized.contains("ghar") ||
            normalized.contains("dashboard") ||
            normalized.contains("main") -> "home"
            
            // Default: return normalized
            else -> normalized
        }
    }
    
    /**
     * Check if Gemini STT enhancement is available
     */
    fun isAvailable(): Boolean {
        return generativeModel != null
    }
}

