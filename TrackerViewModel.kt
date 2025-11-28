package com.nischint.app.ui.screens.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.api.ApiClient
import com.nischint.app.data.api.MockData
import com.nischint.app.data.models.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackerUiState(
    val transactions: List<Transaction> = emptyList(),
    val showBusiness: Boolean = true,  // true = Dhanda, false = Ghar
    val isSyncing: Boolean = false,
    val error: String? = null
) {
    val filteredTransactions: List<Transaction>
        get() = transactions.filter { it.isBusiness == showBusiness }
    
    val totalExpense: Int
        get() = filteredTransactions.filter { it.isExpense }.sumOf { it.amount }
    
    val totalIncome: Int
        get() = filteredTransactions.filter { !it.isExpense }.sumOf { it.amount }
}

class TrackerViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()
    
    fun toggleCategory() {
        _uiState.update { it.copy(showBusiness = !it.showBusiness) }
    }
    
    fun simulateSmsSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            
            // Simulate network delay for demo effect
            delay(1500)
            
            try {
                if (ApiClient.shouldUseMock()) {
                    // Use mock data
                    _uiState.update {
                        it.copy(
                            transactions = MockData.transactions,
                            isSyncing = false
                        )
                    }
                } else {
                    // Use real API
                    val response = ApiClient.apiService.simulateSms()
                    _uiState.update {
                        it.copy(
                            transactions = response.transactions,
                            isSyncing = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to mock data
                _uiState.update {
                    it.copy(
                        transactions = MockData.transactions,
                        isSyncing = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun refreshTransactions() {
        simulateSmsSync()
    }
    
    fun clearTransactions() {
        _uiState.update { it.copy(transactions = emptyList()) }
    }
}
