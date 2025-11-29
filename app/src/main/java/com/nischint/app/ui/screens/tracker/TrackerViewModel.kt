package com.nischint.app.ui.screens.tracker

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.api.ApiClient
import com.nischint.app.data.api.MockData
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.data.database.TransactionEntity
import com.nischint.app.data.models.Transaction
import com.nischint.app.utils.SmsParser
import com.nischint.app.utils.ScreenshotParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackerUiState(
    val transactions: List<Transaction> = emptyList(),
    val showBusiness: Boolean = true,  // true = Dhanda, false = Ghar
    val isSyncing: Boolean = false,
    val isParsingScreenshot: Boolean = false,
    val error: String? = null
) {
    val filteredTransactions: List<Transaction>
        get() = transactions.filter { it.isBusiness == showBusiness }
    
    val totalExpense: Int
        get() = filteredTransactions.filter { it.isExpense }.sumOf { it.amount }
    
    val totalIncome: Int
        get() = filteredTransactions.filter { !it.isExpense }.sumOf { it.amount }
}

class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val context = application.applicationContext
    
    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()
    
    private val smsParser: SmsParser by lazy {
        SmsParser(context.contentResolver)
    }
    
    private val screenshotParser: ScreenshotParser by lazy {
        ScreenshotParser(context)
    }
    
    init {
        // Load transactions from database on initialization
        loadTransactionsFromDatabase()
    }
    
    /**
     * Load transactions from database
     */
    private fun loadTransactionsFromDatabase() {
        viewModelScope.launch {
            database.transactionDao().getAllTransactions()
                .map { entities -> entities.map { it.toTransaction() } }
                .collect { transactions ->
                    _uiState.update { it.copy(transactions = transactions) }
                }
        }
    }
    
    fun toggleCategory() {
        _uiState.update { it.copy(showBusiness = !it.showBusiness) }
    }
    
    /**
     * Parse SMS messages and extract transactions
     */
    fun parseSmsTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            
            try {
                val parsedTransactions = smsParser.parseTransactions()
                
                // Save to database
                val entities = parsedTransactions.map { TransactionEntity.fromTransaction(it) }
                database.transactionDao().insertTransactions(entities)
                
                // Transactions will be loaded automatically via Flow
                _uiState.update {
                    it.copy(isSyncing = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        error = e.message ?: "Error parsing SMS"
                    )
                }
            }
        }
    }
    
    /**
     * Parse screenshot and add transaction
     */
    fun parseScreenshot(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsingScreenshot = true, error = null) }
            
            try {
                val transaction = screenshotParser.parseScreenshot(uri)
                if (transaction != null) {
                    // Save to database
                    val entity = TransactionEntity.fromTransaction(transaction)
                    database.transactionDao().upsertTransaction(entity)
                    
                    // Transaction will be loaded automatically via Flow
                    _uiState.update {
                        it.copy(isParsingScreenshot = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isParsingScreenshot = false,
                            error = "Could not extract transaction from screenshot"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isParsingScreenshot = false,
                        error = e.message ?: "Error parsing screenshot"
                    )
                }
            }
        }
    }
    
    /**
     * Legacy method - kept for compatibility
     */
    fun simulateSmsSync() {
        parseSmsTransactions()
    }
    
    fun refreshTransactions() {
        parseSmsTransactions()
    }
    
    /**
     * Add a manual transaction
     */
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                val entity = TransactionEntity.fromTransaction(transaction)
                database.transactionDao().upsertTransaction(entity)
                // Transaction will be loaded automatically via Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error adding transaction")
                }
            }
        }
    }
    
    /**
     * Delete a transaction
     */
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                database.transactionDao().deleteTransactionById(transactionId)
                // Transaction will be removed automatically via Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error deleting transaction")
                }
            }
        }
    }
    
    fun clearTransactions() {
        viewModelScope.launch {
            try {
                database.transactionDao().deleteAllTransactions()
                // Transactions will be cleared automatically via Flow
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error clearing transactions")
                }
            }
        }
    }
}
