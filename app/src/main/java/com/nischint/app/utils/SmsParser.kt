package com.nischint.app.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import com.nischint.app.data.models.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SMS Parser for extracting transaction data from SMS messages
 * Supports Indian banks and payment apps (UPI, Paytm, PhonePe, etc.)
 */
class SmsParser(private val contentResolver: ContentResolver) {
    
    companion object {
        private const val TAG = "SmsParser"
        
        // Common bank and payment app sender patterns
        private val BANK_PATTERNS = listOf(
            "AXIS", "HDFC", "ICICI", "SBI", "PNB", "BOI", "UBI", "BOB", "CANARA", "UNION",
            "PAYTM", "PHONEPE", "GOOGLEPAY", "GPay", "BHIM", "UPI", "RZPAY", "RAZORPAY"
        )
        
        // Transaction keywords in Hindi/English
        private val DEBIT_KEYWORDS = listOf(
            "debited", "debit", "paid", "spent", "withdrawn", "deducted",
            "nikala", "kharcha", "paid", "bheja", "transfer"
        )
        
        private val CREDIT_KEYWORDS = listOf(
            "credited", "credit", "received", "deposited", "added",
            "mila", "aaya", "received", "credit", "deposit"
        )
    }
    
    /**
     * Read all SMS messages from device
     */
    suspend fun readAllSms(): List<SmsMessage> = withContext(Dispatchers.IO) {
        val messages = mutableListOf<SmsMessage>()
        
        try {
            val uri = Uri.parse("content://sms/inbox")
            val cursor: Cursor? = contentResolver.query(
                uri,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC LIMIT 500" // Last 500 messages
            )
            
            cursor?.use {
                val idIndex = it.getColumnIndex(Telephony.Sms._ID)
                val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
                
                while (it.moveToNext()) {
                    val id = it.getString(idIndex)
                    val address = it.getString(addressIndex) ?: ""
                    val body = it.getString(bodyIndex) ?: ""
                    val date = it.getLong(dateIndex)
                    
                    messages.add(
                        SmsMessage(
                            id = id,
                            sender = address,
                            body = body,
                            timestamp = date
                        )
                    )
                }
            }
            
            Log.d(TAG, "Read ${messages.size} SMS messages")
        } catch (e: Exception) {
            Log.e(TAG, "Error reading SMS", e)
        }
        
        messages
    }
    
    /**
     * Parse SMS messages and extract transactions
     */
    suspend fun parseTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val transactions = mutableListOf<Transaction>()
        val smsMessages = readAllSms()
        
        smsMessages.forEach { sms ->
            val transaction = parseSmsToTransaction(sms)
            transaction?.let { transactions.add(it) }
        }
        
        Log.d(TAG, "Parsed ${transactions.size} transactions from SMS")
        transactions
    }
    
    /**
     * Parse a single SMS message to Transaction
     */
    private fun parseSmsToTransaction(sms: SmsMessage): Transaction? {
        val body = sms.body.uppercase()
        val sender = sms.sender.uppercase()
        
        // Check if SMS is from a bank or payment app
        val isBankSms = BANK_PATTERNS.any { pattern ->
            sender.contains(pattern, ignoreCase = true) || body.contains(pattern, ignoreCase = true)
        }
        
        if (!isBankSms) return null
        
        // Extract amount
        val amount = extractAmount(body) ?: return null
        
        // Determine transaction type
        val isDebit = DEBIT_KEYWORDS.any { body.contains(it, ignoreCase = true) }
        val isCredit = CREDIT_KEYWORDS.any { body.contains(it, ignoreCase = true) }
        
        if (!isDebit && !isCredit) return null
        
        // Extract merchant/description
        val merchant = extractMerchant(body, sender)
        
        // Extract category
        val category = categorizeTransaction(body, merchant)
        
        // Extract date
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(sms.timestamp))
        
        return Transaction(
            id = sms.id,
            type = if (isDebit) "expense" else "income",
            amount = amount,
            category = category,
            merchant = merchant,
            isBusiness = false, // Default, can be updated later
            timestamp = date
        )
    }
    
    /**
     * Extract amount from SMS body
     * Supports formats: ₹100, Rs.100, INR 100, 100.00, etc.
     */
    private fun extractAmount(body: String): Int? {
        // Pattern: ₹ or Rs. or INR followed by number
        val patterns = listOf(
            Regex("""[₹R][Ss]?\.?\s*(\d+(?:\.\d{2})?)"""),
            Regex("""INR\s*(\d+(?:\.\d{2})?)"""),
            Regex("""(\d+(?:\.\d{2})?)\s*[₹R][Ss]?"""),
            Regex("""(\d{1,3}(?:,\d{2,3})*(?:\.\d{2})?)""") // Plain number with commas
        )
        
        for (pattern in patterns) {
            val match = pattern.find(body)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "").replace(".", "")
                return amountStr.toIntOrNull()
            }
        }
        
        return null
    }
    
    /**
     * Extract merchant name from SMS body
     */
    private fun extractMerchant(body: String, sender: String): String {
        // Try to extract merchant name from common patterns
        val patterns = listOf(
            Regex("""(?:to|at|from|via)\s+([A-Z][A-Z\s]{2,20})""", RegexOption.IGNORE_CASE),
            Regex("""(?:merchant|vendor|shop):\s*([A-Z][A-Z\s]{2,20})""", RegexOption.IGNORE_CASE),
            Regex("""([A-Z][A-Z\s]{3,15})\s+(?:UPI|PAYMENT|TRANSACTION)""", RegexOption.IGNORE_CASE)
        )
        
        for (pattern in patterns) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        
        // Fallback: use sender name or generic
        return sender.takeIf { it.isNotBlank() } ?: "Unknown Merchant"
    }
    
    /**
     * Categorize transaction based on SMS content
     */
    private fun categorizeTransaction(body: String, merchant: String): String {
        val lowerBody = body.lowercase()
        val lowerMerchant = merchant.lowercase()
        
        return when {
            lowerBody.contains("petrol") || lowerBody.contains("fuel") || lowerMerchant.contains("petrol") -> "fuel"
            lowerBody.contains("food") || lowerBody.contains("restaurant") || lowerMerchant.contains("zomato") || lowerMerchant.contains("swiggy") -> "food"
            lowerBody.contains("grocery") || lowerMerchant.contains("bigbasket") || lowerMerchant.contains("grofers") -> "grocery"
            lowerBody.contains("recharge") || lowerBody.contains("prepaid") || lowerBody.contains("postpaid") -> "recharge"
            lowerBody.contains("emi") || lowerBody.contains("loan") || lowerBody.contains("installment") -> "emi"
            lowerBody.contains("rent") || lowerBody.contains("house") -> "rent"
            lowerBody.contains("shopping") || lowerMerchant.contains("amazon") || lowerMerchant.contains("flipkart") -> "shopping"
            lowerBody.contains("transport") || lowerBody.contains("uber") || lowerBody.contains("ola") -> "transport"
            lowerBody.contains("entertainment") || lowerBody.contains("movie") || lowerBody.contains("netflix") -> "entertainment"
            else -> "other"
        }
    }
}

/**
 * Data class for SMS message
 */
data class SmsMessage(
    val id: String,
    val sender: String,
    val body: String,
    val timestamp: Long
)
