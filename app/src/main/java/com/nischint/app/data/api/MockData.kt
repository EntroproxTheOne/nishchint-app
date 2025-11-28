package com.nischint.app.data.api

import com.nischint.app.data.models.*

/**
 * Mock data for development and demo.
 * Use this when backend is unavailable or for testing.
 */
object MockData {
    
    // ============ DASHBOARD ============
    
    val dashboard = DashboardResponse(
        name = "Rohan",
        safeToSpend = 2500,
        riskLevel = "green",
        prediction = Prediction(
            expenseLow = 12000,
            expenseHigh = 14500,
            confidence = 0.90f,
            message = "Stable week ahead! Koi tension nahi 😊"
        ),
        goal = Goal(
            name = "Hero Splendor+",
            target = 35000,
            saved = 12250,
            progress = 0.35f,
            streakDays = 3
        )
    )
    
    // ============ TRANSACTIONS ============
    
    val transactions = listOf(
        Transaction(
            id = "txn_1",
            type = "expense",
            amount = 150,
            category = "food",
            merchant = "Zomato",
            isBusiness = false,
            timestamp = "2024-11-28T14:30:00"
        ),
        Transaction(
            id = "txn_2",
            type = "expense",
            amount = 200,
            category = "fuel",
            merchant = "HP Petrol Pump",
            isBusiness = true,
            timestamp = "2024-11-28T10:15:00"
        ),
        Transaction(
            id = "txn_3",
            type = "income",
            amount = 100,
            category = "tips",
            merchant = "Customer Tip",
            isBusiness = true,
            timestamp = "2024-11-28T13:45:00"
        ),
        Transaction(
            id = "txn_4",
            type = "expense",
            amount = 50,
            category = "food",
            merchant = "Chai Tapri",
            isBusiness = false,
            timestamp = "2024-11-28T09:00:00"
        ),
        Transaction(
            id = "txn_5",
            type = "expense",
            amount = 320,
            category = "grocery",
            merchant = "D-Mart",
            isBusiness = false,
            timestamp = "2024-11-27T18:30:00"
        ),
        Transaction(
            id = "txn_6",
            type = "income",
            amount = 450,
            category = "tips",
            merchant = "Zomato Payout",
            isBusiness = true,
            timestamp = "2024-11-27T21:00:00"
        ),
        Transaction(
            id = "txn_7",
            type = "expense",
            amount = 99,
            category = "recharge",
            merchant = "Jio Recharge",
            isBusiness = false,
            timestamp = "2024-11-27T11:00:00"
        ),
        Transaction(
            id = "txn_8",
            type = "expense",
            amount = 250,
            category = "fuel",
            merchant = "Indian Oil",
            isBusiness = true,
            timestamp = "2024-11-26T08:30:00"
        ),
        Transaction(
            id = "txn_9",
            type = "expense",
            amount = 1500,
            category = "emi",
            merchant = "Bajaj Finance EMI",
            isBusiness = false,
            timestamp = "2024-11-25T10:00:00"
        ),
        Transaction(
            id = "txn_10",
            type = "income",
            amount = 5000,
            category = "tips",
            merchant = "Weekly Settlement",
            isBusiness = true,
            timestamp = "2024-11-25T00:00:00"
        )
    )
    
    val smsResponse = SmsResponse(transactions = transactions)
    
    // ============ ONBOARDING ============
    
    val onboardingQuestions = listOf(
        OnboardingQuestion(
            id = "q1",
            text = "Kya tum delivery ka kaam karte ho? 🛵",
            options = listOf("Haan, Zomato/Swiggy", "Haan, Amazon/Flipkart", "Nahi, kuch aur")
        ),
        OnboardingQuestion(
            id = "q2",
            text = "Tumhara apna bike hai ya rent pe hai?",
            options = listOf("Apna hai", "Rent pe hai", "Nahi hai")
        ),
        OnboardingQuestion(
            id = "q3",
            text = "Mahine mein kitna petrol lagta hai? ⛽",
            options = listOf("₹2000 se kam", "₹2000-4000", "₹4000 se zyada")
        ),
        OnboardingQuestion(
            id = "q4",
            text = "Koi loan ya EMI chal rahi hai?",
            options = listOf("Haan, EMI hai", "Nahi, koi loan nahi")
        ),
        OnboardingQuestion(
            id = "q5",
            text = "Kya save karna chahte ho? 🎯",
            options = listOf("Naya Bike", "Emergency Fund", "Family ke liye", "Kuch nahi socha"),
            isFinal = true
        )
    )
    
    val horoscope = HoroscopeResponse(
        predictedExpense = "₹12,000 - ₹14,500",
        savingsPotential = "₹3,500",
        riskAreas = listOf(
            "Petrol pe zyada kharcha",
            "Food delivery frequent hai"
        ),
        tip = "Roz ₹50 bachao, 6 mahine mein bike ka down payment ready! 🏍️"
    )
    
    // ============ HELPER FUNCTIONS ============
    
    fun getTransactionsByCategory(isBusiness: Boolean): List<Transaction> {
        return transactions.filter { it.isBusiness == isBusiness }
    }
    
    fun getTodayTransactions(): List<Transaction> {
        // In real app, filter by today's date
        return transactions.take(4)
    }
    
    fun getYesterdayTransactions(): List<Transaction> {
        return transactions.drop(4).take(3)
    }
    
    fun getTotalExpenseToday(): Int {
        return getTodayTransactions()
            .filter { it.isExpense }
            .sumOf { it.amount }
    }
    
    fun getTotalIncomeToday(): Int {
        return getTodayTransactions()
            .filter { !it.isExpense }
            .sumOf { it.amount }
    }
}
