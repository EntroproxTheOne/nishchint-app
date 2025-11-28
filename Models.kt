package com.nischint.app.data.models

import com.google.gson.annotations.SerializedName

// ============ DASHBOARD ============

data class DashboardResponse(
    val name: String,
    @SerializedName("safe_to_spend") val safeToSpend: Int,
    @SerializedName("risk_level") val riskLevel: String,
    val prediction: Prediction,
    val goal: Goal
)

data class Prediction(
    @SerializedName("expense_low") val expenseLow: Int,
    @SerializedName("expense_high") val expenseHigh: Int,
    val confidence: Float,
    val message: String
)

data class Goal(
    val name: String,
    val target: Int,
    val saved: Int,
    val progress: Float,
    @SerializedName("streak_days") val streakDays: Int
)

// ============ TRANSACTIONS ============

data class SmsResponse(
    val transactions: List<Transaction>
)

data class Transaction(
    val id: String,
    val type: String,  // "expense" or "income"
    val amount: Int,
    val category: String,
    val merchant: String,
    @SerializedName("is_business") val isBusiness: Boolean,
    val timestamp: String = ""
) {
    val isExpense: Boolean get() = type == "expense"
    
    val categoryEmoji: String get() = when (category.lowercase()) {
        "food" -> "🍔"
        "fuel" -> "⛽"
        "grocery" -> "🛒"
        "tips" -> "💰"
        "transport" -> "🚗"
        "recharge" -> "📱"
        "entertainment" -> "🎬"
        "shopping" -> "🛍️"
        "emi" -> "🏦"
        "rent" -> "🏠"
        else -> "💸"
    }
}

// ============ ONBOARDING ============

data class OnboardingQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    @SerializedName("is_final") val isFinal: Boolean = false
)

data class AnswerRequest(
    @SerializedName("question_id") val questionId: String,
    val answer: String
)

data class QuestionResponse(
    @SerializedName("next_question") val nextQuestion: OnboardingQuestion?,
    @SerializedName("is_complete") val isComplete: Boolean
)

data class HoroscopeResponse(
    @SerializedName("predicted_expense") val predictedExpense: String,
    @SerializedName("savings_potential") val savingsPotential: String,
    @SerializedName("risk_areas") val riskAreas: List<String>,
    val tip: String
)

// ============ USER ============

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val isOnboarded: Boolean = false,
    val language: String = "hi"  // Hindi default
)

// ============ ENUMS ============

enum class RiskLevel {
    GREEN,
    YELLOW,
    RED;
    
    companion object {
        fun fromString(value: String): RiskLevel = when (value.lowercase()) {
            "green" -> GREEN
            "yellow" -> YELLOW
            "red" -> RED
            else -> GREEN
        }
    }
}

enum class TransactionCategory(val emoji: String, val labelHi: String) {
    FOOD("🍔", "Khana"),
    FUEL("⛽", "Petrol"),
    GROCERY("🛒", "Grocery"),
    TIPS("💰", "Tips"),
    TRANSPORT("🚗", "Transport"),
    RECHARGE("📱", "Recharge"),
    ENTERTAINMENT("🎬", "Entertainment"),
    SHOPPING("🛍️", "Shopping"),
    EMI("🏦", "EMI"),
    RENT("🏠", "Rent"),
    OTHER("💸", "Other")
}
