package com.nischint.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.api.ApiClient
import com.nischint.app.data.api.MockData
import com.nischint.app.data.models.DashboardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val dashboard: DashboardResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                if (ApiClient.shouldUseMock()) {
                    // Use mock data
                    _uiState.update { 
                        it.copy(
                            dashboard = MockData.dashboard,
                            isLoading = false
                        )
                    }
                } else {
                    // Use real API
                    val response = ApiClient.apiService.getDashboard()
                    _uiState.update { 
                        it.copy(
                            dashboard = response,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to mock data on error
                _uiState.update { 
                    it.copy(
                        dashboard = MockData.dashboard,
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun addCash(amount: Int) {
        // Update safe to spend locally
        _uiState.update { state ->
            state.dashboard?.let { dashboard ->
                val newSafeToSpend = dashboard.safeToSpend + amount
                state.copy(
                    dashboard = dashboard.copy(safeToSpend = newSafeToSpend)
                )
            } ?: state
        }
        
        // In real app, also call API to persist
        viewModelScope.launch {
            try {
                // ApiClient.apiService.addCash(amount)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun refreshDashboard() {
        loadDashboard()
    }
}
