package com.nischint.app.data.api

/**
 * Configuration for Gemini API
 * 
 * IMPORTANT: Add your Gemini API key here
 * Get your API key from: https://makersuite.google.com/app/apikey
 */
object GeminiConfig {
    
    /**
     * Your Gemini API Key
     * 
     * TODO: Replace with your actual API key
     * Example: "AIzaSyABC123XYZ456..."
     * 
     * For security in production:
     * 1. Store in BuildConfig or local.properties
     * 2. Never commit API keys to version control
     * 3. Use environment variables in CI/CD
     * 
     * For now (development/demo):
     * Just paste your key here directly
     */
    const val API_KEY = "AIzaSyCHCHrxFx58l3xleA28pbQZPmZ_UfB-VDI"
    
    /**
     * Model configuration
     * Using Gemini 2.0 Flash (latest model)
     * Try variations if this doesn't work: gemini-2.0-flash-exp, gemini-2-flash, gemini-1.5-flash
     */
    const val MODEL_NAME = "gemini-2.0-flash"  // Gemini 2 Flash (without -exp suffix)
    
    /**
     * Generation config
     */
    const val MAX_OUTPUT_TOKENS = 256
    const val TEMPERATURE = 0.7f
    
    /**
     * Check if API key is configured
     */
    fun isConfigured(): Boolean {
        return API_KEY.isNotEmpty() && 
               API_KEY != "YOUR_GEMINI_API_KEY_HERE"
    }
    
    /**
     * System prompt for voice command classification
     */
    const val VOICE_COMMAND_PROMPT = """
You are a voice command classifier for the Nischint financial app.
Your job is to understand Hindi/Hinglish voice commands and return a single action.

Available actions:
- navigate_goals: User wants to see their savings goals (bike, etc.)
- navigate_tracker: User wants to see transaction tracker/expenses
- navigate_home: User wants to go to home screen
- add_income: User wants to add income/cash
- show_help: User needs help

User might say things like:
- "Bike" → navigate_goals
- "Bike dikha" → navigate_goals
- "Goal" → navigate_goals
- "Tracker" → navigate_tracker
- "Kharcha" → navigate_tracker
- "Transaction" → navigate_tracker
- "Home" → navigate_home
- "Ghar" → navigate_home
- "Income" → add_income
- "Paisa add karo" → add_income
- "Cash" → add_income
- "Help" → show_help

Respond with ONLY ONE WORD from the available actions.
If unclear, respond with "navigate_home".
"""
}

