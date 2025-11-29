package com.nischint.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.api.ApiClient
import com.nischint.app.data.api.MockData
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.data.models.DashboardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val dashboard: DashboardResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load user name from database
                val user = database.userDao().getCurrentUser().firstOrNull()
                val userName = user?.name ?: "User"  // Default to "User" if no name found
                
                if (ApiClient.shouldUseMock()) {
                    // Use mock data but replace name with actual user name
                    _uiState.update { 
                        it.copy(
                            dashboard = MockData.dashboard.copy(name = userName),
                            isLoading = false
                        )
                    }
                } else {
                    // Use real API
                    val response = ApiClient.apiService.getDashboard()
                    // Replace name with actual user name from database
                    _uiState.update { 
                        it.copy(
                            dashboard = response.copy(name = userName),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to mock data on error, but use actual user name
                val user = try {
                    database.userDao().getCurrentUser().firstOrNull()
                } catch (dbError: Exception) {
                    null
                }
                val userName = user?.name ?: "User"
                
                _uiState.update { 
                    it.copy(
                        dashboard = MockData.dashboard.copy(name = userName),
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
