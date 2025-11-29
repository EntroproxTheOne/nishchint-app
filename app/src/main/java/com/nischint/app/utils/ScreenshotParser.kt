package com.nischint.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.nischint.app.data.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screenshot Parser for extracting transaction data from screenshots
 * Uses OCR and pattern matching to extract transaction details
 * 
 * Note: PyTorch LLM integration will be added when model is available
 */
class ScreenshotParser(private val context: Context) {
    
    companion object {
        private const val TAG = "ScreenshotParser"
    }
    
    /**
     * Parse screenshot image to extract transaction data
     * 
     * Current implementation uses pattern matching
     * Future: Will integrate PyTorch LLM model for better accuracy
     */
    suspend fun parseScreenshot(uri: Uri): Transaction? = withContext(Dispatchers.IO) {
        try {
            // Load bitmap from URI
            val bitmap = loadBitmapFromUri(uri) ?: return@withContext null
            
            // TODO: Integrate PyTorch LLM model here when available
            // For now, use basic OCR-like pattern matching
            
            // Extract text from image (placeholder - will use OCR/LLM)
            val extractedText = extractTextFromImage(bitmap)
            
            // Parse transaction from extracted text
            val transaction = parseTextToTransaction(extractedText)
            
            bitmap.recycle()
            transaction
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing screenshot", e)
            null
        }
    }
    
    /**
     * Load bitmap from URI
     */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap", e)
            null
        }
    }
    
    /**
     * Extract text from image
     * TODO: Integrate OCR or PyTorch LLM model
     */
    private suspend fun extractTextFromImage(bitmap: Bitmap): String {
        // Placeholder: In production, use ML Kit Text Recognition or PyTorch LLM
        // For now, return empty string - will be implemented with model
        Log.d(TAG, "Extracting text from image (${bitmap.width}x${bitmap.height})")
        
        // TODO: Call PyTorch LLM model here
        // val extractedText = pytorchModel.extractText(bitmap)
        
        return "" // Placeholder
    }
    
    /**
     * Parse extracted text to Transaction
     */
    private fun parseTextToTransaction(text: String): Transaction? {
        if (text.isBlank()) return null
        
        // Extract amount
        val amount = extractAmount(text) ?: return null
        
        // Extract merchant/description
        val merchant = extractMerchant(text)
        
        // Extract category
        val category = categorizeTransaction(text, merchant)
        
        // Extract date (default to current date)
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        // Determine transaction type (default to expense)
        val type = if (text.contains("credit", ignoreCase = true) || 
                       text.contains("received", ignoreCase = true)) {
            "income"
        } else {
            "expense"
        }
        
        return Transaction(
            id = UUID.randomUUID().toString(),
            type = type,
            amount = amount,
            category = category,
            merchant = merchant,
            isBusiness = false,
            timestamp = date
        )
    }
    
    /**
     * Extract amount from text
     */
    private fun extractAmount(text: String): Int? {
        val patterns = listOf(
            Regex("""[₹R][Ss]?\.?\s*(\d+(?:\.\d{2})?)"""),
            Regex("""INR\s*(\d+(?:\.\d{2})?)"""),
            Regex("""(\d+(?:\.\d{2})?)\s*[₹R][Ss]?"""),
            Regex("""(\d{1,3}(?:,\d{2,3})*(?:\.\d{2})?)""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "").replace(".", "")
                return amountStr.toIntOrNull()
            }
        }
        
        return null
    }
    
    /**
     * Extract merchant name from text
     */
    private fun extractMerchant(text: String): String {
        val patterns = listOf(
            Regex("""(?:to|at|from|via)\s+([A-Z][A-Z\s]{2,20})""", RegexOption.IGNORE_CASE),
            Regex("""(?:merchant|vendor|shop):\s*([A-Z][A-Z\s]{2,20})""", RegexOption.IGNORE_CASE)
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        
        return "Screenshot Transaction"
    }
    
    /**
     * Categorize transaction
     */
    private fun categorizeTransaction(text: String, merchant: String): String {
        val lowerText = text.lowercase()
        val lowerMerchant = merchant.lowercase()
        
        return when {
            lowerText.contains("petrol") || lowerText.contains("fuel") -> "fuel"
            lowerText.contains("food") || lowerText.contains("restaurant") -> "food"
            lowerText.contains("grocery") -> "grocery"
            lowerText.contains("recharge") -> "recharge"
            lowerText.contains("emi") || lowerText.contains("loan") -> "emi"
            lowerText.contains("rent") -> "rent"
            lowerText.contains("shopping") -> "shopping"
            lowerText.contains("transport") -> "transport"
            lowerText.contains("entertainment") -> "entertainment"
            else -> "other"
        }
    }
}

/**
 * Composable hook for image picker
 */
@Composable
fun rememberImagePicker(
    onImageSelected: (Uri?) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }
    
    return {
        launcher.launch("image/*")
    }
}
