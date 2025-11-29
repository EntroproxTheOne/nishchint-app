package com.nischint.app.data.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.nischint.app.data.models.OnboardingQuestion
import com.nischint.app.data.preferences.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Generates onboarding questions using Gemini API
 * Questions are in native Hindi style with conversational tone
 */
class OnboardingQuestionGenerator {
    
    companion object {
        private const val TAG = "OnboardingQuestionGen"
        
        // System prompt for generating onboarding questions
        private fun getOnboardingPrompt(language: AppLanguage): String {
            return when (language) {
                AppLanguage.HINDI -> """
You are a friendly financial assistant for delivery workers in India. Your name is Nischint.
You speak in native Hindi/Hinglish style - casual, friendly, and relatable.

Generate onboarding questions for a financial management app. Each question should:
1. Be in native Hindi/Hinglish style (casual, friendly, like talking to a friend)
2. Use "tum" instead of "aap" (informal, friendly)
3. Include emojis where appropriate
4. Be conversational and engaging
5. Have 2-4 options in Hindi/Hinglish

Generate questions in this JSON format:
{
  "questions": [
    {
      "id": "q1",
      "text": "Question text in Hindi/Hinglish",
      "options": ["Option 1", "Option 2", "Option 3"],
      "isFinal": false
    }
  ]
}

Generate 5 questions about:
1. Delivery work (Zomato/Swiggy/Amazon/Flipkart)
2. Vehicle ownership (own bike/rent/no bike)
3. Monthly expenses (petrol, food, etc.)
4. Loans/EMIs
5. Savings goals (bike, emergency fund, family, etc.) - mark this as final

Return ONLY valid JSON, no other text.
"""
                AppLanguage.ENGLISH -> """
You are a friendly financial assistant for delivery workers. Your name is Nischint.
You speak in a casual, friendly, and relatable style.

Generate onboarding questions for a financial management app. Each question should:
1. Be casual and friendly (like talking to a friend)
2. Include emojis where appropriate
3. Be conversational and engaging
4. Have 2-4 options

Generate questions in this JSON format:
{
  "questions": [
    {
      "id": "q1",
      "text": "Question text in English",
      "options": ["Option 1", "Option 2", "Option 3"],
      "isFinal": false
    }
  ]
}

Generate 5 questions about:
1. Delivery work (Zomato/Swiggy/Amazon/Flipkart)
2. Vehicle ownership (own bike/rent/no bike)
3. Monthly expenses (petrol, food, etc.)
4. Loans/EMIs
5. Savings goals (bike, emergency fund, family, etc.) - mark this as final

Return ONLY valid JSON, no other text.
"""
            }
        }
    }
    
    private val generativeModel: GenerativeModel? by lazy {
        if (GeminiConfig.isConfigured()) {
            // Try multiple model names in order of preference
            val modelNamesToTry = listOf(
                GeminiConfig.MODEL_NAME,  // Primary: gemini-2.0-flash
                "gemini-2.0-flash-exp",    // Fallback 1: experimental version
                "gemini-1.5-flash",        // Fallback 2: stable flash
                "gemini-1.5-pro"           // Fallback 3: stable pro
            )
            
            for (modelName in modelNamesToTry) {
                try {
                    Log.d(TAG, "Attempting to initialize Gemini model: $modelName")
                    val model = GenerativeModel(
                        modelName = modelName,
                        apiKey = GeminiConfig.API_KEY,
                        generationConfig = generationConfig {
                            temperature = 0.8f  // More creative for conversational Hindi
                            maxOutputTokens = 2048  // Increased for better JSON responses
                            topP = 0.95f
                            topK = 40
                        }
                    )
                    Log.d(TAG, "✓ Successfully initialized Gemini model: $modelName")
                    return@lazy model
                } catch (e: Exception) {
                    Log.w(TAG, "✗ Failed to initialize model '$modelName': ${e.message}")
                    if (modelName == modelNamesToTry.last()) {
                        // Last attempt failed, log error and return null
                        Log.e(TAG, "All model initialization attempts failed", e)
                        Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                        e.printStackTrace()
                    }
                    // Continue to next model name
                }
            }
            null
        } else {
            Log.w(TAG, "Gemini API not configured, using fallback questions")
            Log.w(TAG, "API Key empty: ${GeminiConfig.API_KEY.isEmpty()}")
            null
        }
    }
    
    /**
     * Generate onboarding questions using Gemini API
     * Falls back to MockData questions if API is unavailable
     * Returns a Pair: (questions, isAiGenerated)
     */
    fun generateQuestions(language: AppLanguage = AppLanguage.HINDI): Flow<Pair<List<OnboardingQuestion>, Boolean>> = flow {
        if (generativeModel == null) {
            Log.d(TAG, "Using fallback questions from MockData")
            emit(Pair(MockData.onboardingQuestions, false))  // Not AI-generated
            return@flow
        }
        
        try {
            val questions = generateWithGemini(language)
            emit(Pair(questions, true))  // AI-generated
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate questions with Gemini, using fallback", e)
            emit(Pair(getFallbackQuestions(language), false))  // Not AI-generated
        }
    }.flowOn(Dispatchers.IO)  // Run API calls on IO dispatcher to prevent cancellation
    
    /**
     * Generate questions using Gemini AI
     */
    private suspend fun generateWithGemini(language: AppLanguage): List<OnboardingQuestion> {
        return try {
            val prompt = getOnboardingPrompt(language)
            
            if (generativeModel == null) {
                Log.e(TAG, "GenerativeModel is null! Cannot generate questions.")
                return MockData.onboardingQuestions
            }
            
            Log.d(TAG, "Requesting questions from Gemini...")
            Log.d(TAG, "Model config: ${GeminiConfig.MODEL_NAME}")
            Log.d(TAG, "API Key configured: ${GeminiConfig.isConfigured()}")
            Log.d(TAG, "API Key preview: ${GeminiConfig.API_KEY.take(10)}...")
            
            val response = generativeModel?.generateContent(prompt)
            
            if (response == null) {
                Log.e(TAG, "Gemini response is null")
                return MockData.onboardingQuestions
            }
            
            val responseText = response.text?.trim() ?: ""
            
            if (responseText.isEmpty()) {
                Log.e(TAG, "Gemini response text is empty")
                Log.e(TAG, "Response candidates: ${response.candidates}")
                Log.e(TAG, "Response finish reason: ${response.candidates?.firstOrNull()?.finishReason}")
                return MockData.onboardingQuestions
            }
            
            Log.d(TAG, "Gemini response length: ${responseText.length}")
            Log.d(TAG, "Gemini response preview: ${responseText.take(200)}...")
            
            // Parse JSON response
            val questions = parseQuestionsFromJson(responseText)
            
            if (questions.isNotEmpty()) {
                Log.d(TAG, "Successfully generated ${questions.size} questions from Gemini")
                questions
            } else {
                Log.w(TAG, "Failed to parse questions from response, using fallback")
                Log.w(TAG, "Full response: $responseText")
                getFallbackQuestions(language)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating questions with Gemini", e)
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Exception message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    /**
     * Get fallback questions based on language
     */
    private fun getFallbackQuestions(language: AppLanguage): List<OnboardingQuestion> {
        return when (language) {
            AppLanguage.HINDI -> parseQuestionsFallback("")
            AppLanguage.ENGLISH -> listOf(
                OnboardingQuestion(
                    id = "q1",
                    text = "Do you do delivery work? 🛵",
                    options = listOf("Yes, Zomato/Swiggy", "Yes, Amazon/Flipkart", "No, something else"),
                    isFinal = false,
                    isAiGenerated = false
                ),
                OnboardingQuestion(
                    id = "q2",
                    text = "Do you own a bike or rent one?",
                    options = listOf("Own it", "Rent it", "Don't have one"),
                    isFinal = false,
                    isAiGenerated = false
                ),
                OnboardingQuestion(
                    id = "q3",
                    text = "How much petrol do you spend per month? ⛽",
                    options = listOf("Less than ₹2000", "₹2000-4000", "More than ₹4000"),
                    isFinal = false,
                    isAiGenerated = false
                ),
                OnboardingQuestion(
                    id = "q4",
                    text = "Do you have any loans or EMIs?",
                    options = listOf("Yes, have EMI", "No loans"),
                    isFinal = false,
                    isAiGenerated = false
                ),
                OnboardingQuestion(
                    id = "q5",
                    text = "What do you want to save for? 🎯",
                    options = listOf("New Bike", "Emergency Fund", "For Family", "Haven't thought"),
                    isFinal = true,
                    isAiGenerated = false
                )
            )
        }
    }
    
    /**
     * Parse questions from JSON response using Gson
     * Handles various JSON formats that Gemini might return
     */
    private fun parseQuestionsFromJson(jsonText: String): List<OnboardingQuestion> {
        return try {
            // Extract JSON from markdown code blocks if present
            var cleanJson = jsonText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // Try to find JSON object in the response
            val jsonStart = cleanJson.indexOf('{')
            val jsonEnd = cleanJson.lastIndexOf('}')
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                cleanJson = cleanJson.substring(jsonStart, jsonEnd + 1)
            }
            
            Log.d(TAG, "Parsing JSON: ${cleanJson.take(300)}...")
            
            // Parse using Gson
            val gson = Gson()
            val jsonParser = JsonParser()
            val jsonElement = jsonParser.parse(cleanJson)
            
            val questions = mutableListOf<OnboardingQuestion>()
            
            when {
                // If root is an object with "questions" array
                jsonElement.isJsonObject -> {
                    val jsonObject = jsonElement.asJsonObject
                    if (jsonObject.has("questions") && jsonObject.get("questions").isJsonArray) {
                        val questionsArray = jsonObject.getAsJsonArray("questions")
                        questionsArray.forEach { questionElement ->
                            parseQuestionElement(questionElement, questions)
                        }
                    } else {
                        // Try parsing as direct question object
                        parseQuestionElement(jsonElement, questions)
                    }
                }
                // If root is an array of questions
                jsonElement.isJsonArray -> {
                    val questionsArray = jsonElement.asJsonArray
                    questionsArray.forEach { questionElement ->
                        parseQuestionElement(questionElement, questions)
                    }
                }
            }
            
            Log.d(TAG, "Parsed ${questions.size} questions from JSON")
            
            // If parsing failed, try fallback
            if (questions.isEmpty()) {
                Log.w(TAG, "Gson parsing failed, trying fallback parsing")
                questions.addAll(parseQuestionsFallback(cleanJson))
            }
            
            questions
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON with Gson", e)
            Log.e(TAG, "JSON text: ${jsonText.take(500)}")
            e.printStackTrace()
            
            // Try fallback parsing
            try {
                parseQuestionsFallback(jsonText)
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Fallback parsing also failed", fallbackError)
                emptyList()
            }
        }
    }
    
    /**
     * Parse a single question element from JSON
     */
    private fun parseQuestionElement(element: com.google.gson.JsonElement, questions: MutableList<OnboardingQuestion>) {
        try {
            if (!element.isJsonObject) return
            
            val questionObj = element.asJsonObject
            
            val id = questionObj.get("id")?.asString ?: "q${questions.size + 1}"
            val text = questionObj.get("text")?.asString ?: return
            val isFinal = questionObj.get("isFinal")?.asBoolean ?: false
            
            val options = mutableListOf<String>()
            val optionsElement = questionObj.get("options")
            
            when {
                optionsElement?.isJsonArray == true -> {
                    optionsElement.asJsonArray.forEach { optionElement ->
                        if (optionElement.isJsonPrimitive) {
                            options.add(optionElement.asString)
                        }
                    }
                }
                optionsElement?.isJsonPrimitive == true -> {
                    options.add(optionsElement.asString)
                }
            }
            
            if (options.isNotEmpty()) {
                questions.add(
                    OnboardingQuestion(
                        id = id,
                        text = text,
                        options = options,
                        isFinal = isFinal,
                        isAiGenerated = true
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing question element", e)
        }
    }
    
    /**
     * Fallback parsing method
     */
    private fun parseQuestionsFallback(jsonText: String): List<OnboardingQuestion> {
        // If parsing fails, return enhanced MockData questions with better Hindi
        return listOf(
            OnboardingQuestion(
                id = "q1",
                text = "Kya tum delivery ka kaam karte ho? 🛵",
                options = listOf("Haan, Zomato/Swiggy", "Haan, Amazon/Flipkart", "Nahi, kuch aur"),
                isFinal = false,
                isAiGenerated = true  // Mark as AI-generated (from Gemini fallback)
            ),
            OnboardingQuestion(
                id = "q2",
                text = "Tumhara apna bike hai ya rent pe hai?",
                options = listOf("Apna hai", "Rent pe hai", "Nahi hai"),
                isFinal = false,
                isAiGenerated = true
            ),
            OnboardingQuestion(
                id = "q3",
                text = "Mahine mein kitna petrol lagta hai? ⛽",
                options = listOf("₹2000 se kam", "₹2000-4000", "₹4000 se zyada"),
                isFinal = false,
                isAiGenerated = true
            ),
            OnboardingQuestion(
                id = "q4",
                text = "Koi loan ya EMI chal rahi hai?",
                options = listOf("Haan, EMI hai", "Nahi, koi loan nahi"),
                isFinal = false,
                isAiGenerated = true
            ),
            OnboardingQuestion(
                id = "q5",
                text = "Kya save karna chahte ho? 🎯",
                options = listOf("Naya Bike", "Emergency Fund", "Family ke liye", "Kuch nahi socha"),
                isFinal = true,
                isAiGenerated = true
            )
        )
    }
    
    /**
     * Generate a personalized greeting message
     */
    suspend fun generateGreeting(userName: String? = null, language: AppLanguage = AppLanguage.HINDI): String {
        if (generativeModel == null) {
            return when (language) {
                AppLanguage.HINDI -> "Namaste! 🙏 Main Nischint hoon, tera financial buddy!"
                AppLanguage.ENGLISH -> "Hello! 🙏 I'm Nischint, your financial buddy!"
            }
        }
        
        return try {
            val prompt = when (language) {
                AppLanguage.HINDI -> """
Generate a friendly greeting in native Hindi/Hinglish for a financial app.
Be casual, friendly, and use "tum" instead of "aap".
Include an emoji.
Keep it short (1-2 sentences).
${if (userName != null) "User's name: $userName" else ""}

Return ONLY the greeting text, no other text.
"""
                AppLanguage.ENGLISH -> """
Generate a friendly greeting in English for a financial app.
Be casual and friendly.
Include an emoji.
Keep it short (1-2 sentences).
${if (userName != null) "User's name: $userName" else ""}

Return ONLY the greeting text, no other text.
"""
            }
            
            val response = generativeModel?.generateContent(prompt)
            response?.text?.trim() ?: when (language) {
                AppLanguage.HINDI -> "Namaste! 🙏 Main Nischint hoon, tera financial buddy!"
                AppLanguage.ENGLISH -> "Hello! 🙏 I'm Nischint, your financial buddy!"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating greeting", e)
            when (language) {
                AppLanguage.HINDI -> "Namaste! 🙏 Main Nischint hoon, tera financial buddy!"
                AppLanguage.ENGLISH -> "Hello! 🙏 I'm Nischint, your financial buddy!"
            }
        }
    }
    
    /**
     * Generate a completion message after onboarding
     */
    suspend fun generateCompletionMessage(userAnswers: Map<String, String>): String {
        if (generativeModel == null) {
            return "Badhiya! Ab tumhara financial journey shuru ho gaya hai! 🚀"
        }
        
        return try {
            val answersSummary = userAnswers.entries.joinToString("\n") { "${it.key}: ${it.value}" }
            
            val prompt = """
Generate a completion message in native Hindi/Hinglish for a financial app onboarding.
Be encouraging, friendly, and use "tum" instead of "aap".
Include an emoji.
Keep it short (1-2 sentences).

User's answers:
$answersSummary

Return ONLY the completion message, no other text.
"""
            
            val response = generativeModel?.generateContent(prompt)
            response?.text?.trim() ?: "Badhiya! Ab tumhara financial journey shuru ho gaya hai! 🚀"
        } catch (e: Exception) {
            Log.e(TAG, "Error generating completion message", e)
            "Badhiya! Ab tumhara financial journey shuru ho gaya hai! 🚀"
        }
    }
}

