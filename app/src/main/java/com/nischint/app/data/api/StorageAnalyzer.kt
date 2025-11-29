package com.nischint.app.data.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.nischint.app.data.models.Transaction
import com.nischint.app.data.preferences.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Storage Analyzer using Gemini AI
 * Analyzes transaction data and provides financial insights
 */
data class StorageAnalysis(
    val summary: String,
    val insights: List<String>,
    val recommendations: List<String>,
    val spendingPattern: String,
    val riskLevel: String,
    val savingsPotential: String
)

class StorageAnalyzer {
    
    companion object {
        private const val TAG = "StorageAnalyzer"
        
        private fun getAnalysisPrompt(language: AppLanguage): String {
            return when (language) {
                AppLanguage.HINDI -> """
You are a financial advisor for delivery workers in India.
Analyze the transaction data and provide insights in native Hindi/Hinglish style.

Be friendly, casual, and use "tum" instead of "aap".
Provide actionable advice.

Analyze:
1. Spending patterns (categories, amounts, frequency)
2. Income vs expenses
3. Business vs personal expenses
4. Potential savings opportunities
5. Risk areas (overspending, unnecessary expenses)
6. Recommendations for better financial management

Return analysis in this JSON format:
{
  "summary": "Brief summary in Hindi/Hinglish",
  "insights": ["Insight 1", "Insight 2", "Insight 3"],
  "recommendations": ["Recommendation 1", "Recommendation 2"],
  "spendingPattern": "Description of spending pattern",
  "riskLevel": "low/medium/high",
  "savingsPotential": "Estimated monthly savings potential"
}

Keep it concise and practical. Use emojis where appropriate.
Return ONLY valid JSON, no other text.
"""
                AppLanguage.ENGLISH -> """
You are a financial advisor for delivery workers.
Analyze the transaction data and provide insights in English.

Be friendly and casual.
Provide actionable advice.

Analyze:
1. Spending patterns (categories, amounts, frequency)
2. Income vs expenses
3. Business vs personal expenses
4. Potential savings opportunities
5. Risk areas (overspending, unnecessary expenses)
6. Recommendations for better financial management

Return analysis in this JSON format:
{
  "summary": "Brief summary in English",
  "insights": ["Insight 1", "Insight 2", "Insight 3"],
  "recommendations": ["Recommendation 1", "Recommendation 2"],
  "spendingPattern": "Description of spending pattern",
  "riskLevel": "low/medium/high",
  "savingsPotential": "Estimated monthly savings potential"
}

Keep it concise and practical. Use emojis where appropriate.
Return ONLY valid JSON, no other text.
"""
            }
        }
    }
    
    private val generativeModel: GenerativeModel? by lazy {
        if (GeminiConfig.isConfigured()) {
            try {
                GenerativeModel(
                    modelName = GeminiConfig.MODEL_NAME,
                    apiKey = GeminiConfig.API_KEY,
                    generationConfig = generationConfig {
                        temperature = 0.7f  // Balanced creativity
                        maxOutputTokens = 1024  // Enough for detailed analysis
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
     * Analyze transactions and generate insights
     */
    fun analyzeTransactions(transactions: List<Transaction>, language: AppLanguage = AppLanguage.HINDI): Flow<StorageAnalysis> = flow {
        if (transactions.isEmpty()) {
            emit(getEmptyAnalysis(language))
            return@flow
        }
        
        Log.d(TAG, "Analyzing ${transactions.size} transactions")
        
        // If Gemini not available, use fallback analysis
        if (generativeModel == null) {
            emit(analyzeWithFallback(transactions, language))
            return@flow
        }
        
        try {
            val analysis = analyzeWithGemini(transactions, language)
            emit(analysis)
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing with Gemini", e)
            emit(analyzeWithFallback(transactions, language))
        }
    }.flowOn(Dispatchers.IO)  // Run API calls on IO dispatcher to prevent cancellation
    
    /**
     * Analyze using Gemini AI
     */
    private suspend fun analyzeWithGemini(transactions: List<Transaction>, language: AppLanguage): StorageAnalysis {
        // Prepare transaction summary
        val transactionSummary = formatTransactionsForAnalysis(transactions)
        
        val prompt = "${getAnalysisPrompt(language)}\n\nTransaction Data:\n$transactionSummary\n\nAnalysis:"
        
        val response = generativeModel?.generateContent(prompt)
        val responseText = response?.text?.trim() ?: ""
        
        Log.d(TAG, "Gemini analysis response: $responseText")
        
        // Parse JSON response
        val analysis = parseAnalysisFromJson(responseText, language)
        
        return analysis ?: analyzeWithFallback(transactions, language)
    }
    
    /**
     * Format transactions for analysis
     */
    private fun formatTransactionsForAnalysis(transactions: List<Transaction>): String {
        val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        
        val categoryBreakdown = transactions
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        
        val businessExpenses = transactions
            .filter { it.isBusiness && it.type == "expense" }
            .sumOf { it.amount }
        
        val personalExpenses = transactions
            .filter { !it.isBusiness && it.type == "expense" }
            .sumOf { it.amount }
        
        return """
Total Income: ₹$totalIncome
Total Expenses: ₹$totalExpense
Net: ₹${totalIncome - totalExpense}

Category Breakdown:
${categoryBreakdown.entries.joinToString("\n") { "- ${it.key}: ₹${it.value}" }}

Business Expenses: ₹$businessExpenses
Personal Expenses: ₹$personalExpenses

Recent Transactions:
${transactions.take(10).joinToString("\n") { "- ${it.category} (${it.type}): ₹${it.amount} at ${it.merchant}" }}
"""
    }
    
    /**
     * Parse analysis from JSON response
     */
    private fun parseAnalysisFromJson(jsonText: String, language: AppLanguage): StorageAnalysis? {
        return try {
            val cleanJson = jsonText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // Simple JSON parsing (for production, use proper JSON library)
            val summary = extractJsonField(cleanJson, "summary") 
                ?: when (language) {
                    AppLanguage.HINDI -> "Tumhara spending pattern analyze kiya gaya hai."
                    AppLanguage.ENGLISH -> "Your spending pattern has been analyzed."
                }
            
            val insights = extractJsonArray(cleanJson, "insights")
                ?: when (language) {
                    AppLanguage.HINDI -> listOf("Transaction data analyzed")
                    AppLanguage.ENGLISH -> listOf("Transaction data analyzed")
                }
            
            val recommendations = extractJsonArray(cleanJson, "recommendations")
                ?: when (language) {
                    AppLanguage.HINDI -> listOf("Regular analysis karte raho")
                    AppLanguage.ENGLISH -> listOf("Keep analyzing regularly")
                }
            
            val spendingPattern = extractJsonField(cleanJson, "spendingPattern")
                ?: "Mixed spending pattern"
            
            val riskLevel = extractJsonField(cleanJson, "riskLevel")
                ?: "medium"
            
            val savingsPotential = extractJsonField(cleanJson, "savingsPotential")
                ?: when (language) {
                    AppLanguage.HINDI -> "₹1000-2000 per month"
                    AppLanguage.ENGLISH -> "₹1000-2000 per month"
                }
            
            StorageAnalysis(
                summary = summary,
                insights = insights,
                recommendations = recommendations,
                spendingPattern = spendingPattern,
                riskLevel = riskLevel,
                savingsPotential = savingsPotential
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing analysis JSON", e)
            null
        }
    }
    
    /**
     * Extract field from JSON
     */
    private fun extractJsonField(json: String, fieldName: String): String? {
        val pattern = Regex("""\"$fieldName\"\s*:\s*\"([^\"]+)\"""")
        return pattern.find(json)?.groupValues?.get(1)
    }
    
    /**
     * Extract array from JSON
     */
    private fun extractJsonArray(json: String, fieldName: String): List<String>? {
        val pattern = Regex("""\"$fieldName\"\s*:\s*\[(.*?)\]""", RegexOption.DOT_MATCHES_ALL)
        val match = pattern.find(json) ?: return null
        
        val arrayContent = match.groupValues[1]
        return arrayContent
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    }
    
    /**
     * Fallback analysis without Gemini
     */
    private fun analyzeWithFallback(transactions: List<Transaction>, language: AppLanguage): StorageAnalysis {
        val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val net = totalIncome - totalExpense
        
        val categoryBreakdown = transactions
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        
        val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key ?: "Unknown"
        
        val riskLevel = when {
            net < 0 -> "high"
            net < totalIncome * 0.2 -> "medium"
            else -> "low"
        }
        
        val savingsPotential = when (riskLevel) {
            "high" -> "₹500-1000 per month"
            "medium" -> "₹1000-2000 per month"
            else -> "₹2000-3000 per month"
        }
        
        return when (language) {
            AppLanguage.HINDI -> StorageAnalysis(
                summary = "Tumhara total income ₹$totalIncome hai aur expenses ₹$totalExpense. Net: ₹$net",
                insights = listOf(
                    "Sabse zyada kharcha $topCategory pe hai",
                    if (net < 0) "Tumhara expenses income se zyada hai" else "Tumhara savings potential accha hai",
                    "Business expenses analyze karo for tax benefits"
                ),
                recommendations = listOf(
                    "Roz ka kharcha track karo",
                    "Unnecessary expenses kam karo",
                    "Savings goal set karo"
                ),
                spendingPattern = "Mixed spending across ${categoryBreakdown.size} categories",
                riskLevel = riskLevel,
                savingsPotential = savingsPotential
            )
            AppLanguage.ENGLISH -> StorageAnalysis(
                summary = "Your total income is ₹$totalIncome and expenses are ₹$totalExpense. Net: ₹$net",
                insights = listOf(
                    "Most spending is on $topCategory",
                    if (net < 0) "Your expenses exceed income" else "Your savings potential is good",
                    "Analyze business expenses for tax benefits"
                ),
                recommendations = listOf(
                    "Track daily expenses",
                    "Reduce unnecessary expenses",
                    "Set savings goals"
                ),
                spendingPattern = "Mixed spending across ${categoryBreakdown.size} categories",
                riskLevel = riskLevel,
                savingsPotential = savingsPotential
            )
        }
    }
    
    /**
     * Empty analysis for no transactions
     */
    private fun getEmptyAnalysis(language: AppLanguage): StorageAnalysis {
        return when (language) {
            AppLanguage.HINDI -> StorageAnalysis(
                summary = "Abhi tak koi transactions nahi hai. Pehla transaction add karo!",
                insights = emptyList(),
                recommendations = listOf(
                    "Transactions add karo to get insights",
                    "Regular tracking se better financial control milega"
                ),
                spendingPattern = "No data yet",
                riskLevel = "low",
                savingsPotential = "Start tracking to see potential"
            )
            AppLanguage.ENGLISH -> StorageAnalysis(
                summary = "No transactions yet. Add your first transaction!",
                insights = emptyList(),
                recommendations = listOf(
                    "Add transactions to get insights",
                    "Regular tracking gives better financial control"
                ),
                spendingPattern = "No data yet",
                riskLevel = "low",
                savingsPotential = "Start tracking to see potential"
            )
        }
    }
    
    /**
     * Check if Gemini analysis is available
     */
    fun isAvailable(): Boolean {
        return generativeModel != null
    }
}

