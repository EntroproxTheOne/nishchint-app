package com.nischint.app.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Voice recognition state
 */
sealed class VoiceRecognitionState {
    object Idle : VoiceRecognitionState()
    object Listening : VoiceRecognitionState()
    data class Result(val transcript: String, val isFinal: Boolean) : VoiceRecognitionState()
    data class Error(val message: String) : VoiceRecognitionState()
}

/**
 * Manager for handling voice recognition using Android SpeechRecognizer
 * 
 * Usage:
 * ```
 * val voiceManager = VoiceRecognitionManager(context)
 * 
 * // Observe state
 * val state = voiceManager.state.collectAsState()
 * 
 * // Start listening
 * voiceManager.startListening()
 * 
 * // Stop listening
 * voiceManager.stopListening()
 * 
 * // Cleanup
 * voiceManager.destroy()
 * ```
 */
class VoiceRecognitionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "VoiceRecognitionMgr"
    }
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    private val _state = MutableStateFlow<VoiceRecognitionState>(VoiceRecognitionState.Idle)
    val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    init {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition not available on this device")
            _state.value = VoiceRecognitionState.Error("Speech recognition not available")
        }
    }
    
    /**
     * Start listening for voice input
     */
    fun startListening() {
        if (!context.hasAudioPermission()) {
            _state.value = VoiceRecognitionState.Error("Microphone permission not granted")
            return
        }
        
        if (_isListening.value) {
            Log.w(TAG, "Already listening")
            return
        }
        
        try {
            // Create speech recognizer if not exists
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(recognitionListener)
                }
            }
            
            // Create intent
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Get partial results
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Bol: 'Bike', 'Tracker', 'Income'..."
                )
            }
            
            // Start listening
            speechRecognizer?.startListening(intent)
            _isListening.value = true
            _state.value = VoiceRecognitionState.Listening
            Log.d(TAG, "Started listening")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            _state.value = VoiceRecognitionState.Error("Failed to start: ${e.message}")
            _isListening.value = false
        }
    }
    
    /**
     * Stop listening for voice input
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _isListening.value = false
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognition", e)
        }
    }
    
    /**
     * Cancel voice recognition
     */
    fun cancel() {
        try {
            speechRecognizer?.cancel()
            _isListening.value = false
            _state.value = VoiceRecognitionState.Idle
            Log.d(TAG, "Cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling speech recognition", e)
        }
    }
    
    /**
     * Clean up resources
     */
    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            _isListening.value = false
            _state.value = VoiceRecognitionState.Idle
            Log.d(TAG, "Destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying speech recognition", e)
        }
    }
    
    /**
     * Recognition listener for handling speech recognition events
     */
    private val recognitionListener = object : RecognitionListener {
        
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "Ready for speech")
            _state.value = VoiceRecognitionState.Listening
        }
        
        override fun onBeginningOfSpeech() {
            Log.d(TAG, "Speech started")
        }
        
        override fun onRmsChanged(rmsdB: Float) {
            // Audio level changed - can be used for visual feedback
        }
        
        override fun onBufferReceived(buffer: ByteArray?) {
            // Audio buffer received
        }
        
        override fun onEndOfSpeech() {
            Log.d(TAG, "Speech ended")
            _isListening.value = false
        }
        
        override fun onError(error: Int) {
            val errorMessage = getErrorMessage(error)
            Log.e(TAG, "Recognition error: $errorMessage (code: $error)")
            
            _isListening.value = false
            
            // Don't show error for user cancellation or no match (common scenarios)
            if (error != SpeechRecognizer.ERROR_NO_MATCH && 
                error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                _state.value = VoiceRecognitionState.Error(errorMessage)
            } else {
                _state.value = VoiceRecognitionState.Idle
            }
        }
        
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val transcript = matches?.firstOrNull() ?: ""
            
            Log.d(TAG, "Final results: $transcript")
            
            if (transcript.isNotEmpty()) {
                _state.value = VoiceRecognitionState.Result(transcript, isFinal = true)
            } else {
                _state.value = VoiceRecognitionState.Idle
            }
            
            _isListening.value = false
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION
            )
            val transcript = matches?.firstOrNull() ?: ""
            
            Log.d(TAG, "Partial results: $transcript")
            
            if (transcript.isNotEmpty()) {
                _state.value = VoiceRecognitionState.Result(transcript, isFinal = false)
            }
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {
            // Additional events
        }
    }
    
    /**
     * Convert error code to user-friendly message
     */
    private fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission needed"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }
}

/**
 * Composable function to remember VoiceRecognitionManager instance
 * Automatically handles cleanup when composable leaves composition
 */
@androidx.compose.runtime.Composable
fun rememberVoiceRecognitionManager(): VoiceRecognitionManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = androidx.compose.runtime.remember { VoiceRecognitionManager(context) }
    
    // Cleanup when composable is disposed
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            manager.destroy()
        }
    }
    
    return manager
}

