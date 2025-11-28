package com.nischint.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.api.MockData
import com.nischint.app.data.models.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalsUiState(
    val goal: Goal? = null,
    val scratchCardRevealed: Boolean = false,
    val reward: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class GoalsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    
    fun loadGoal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Using mock data
            val goal = MockData.dashboard.goal
            
            _uiState.update {
                it.copy(
                    goal = goal,
                    isLoading = false
                )
            }
        }
    }
    
    fun addSavings(amount: Int) {
        _uiState.update { state ->
            state.goal?.let { goal ->
                val newSaved = goal.saved + amount
                val newProgress = (newSaved.toFloat() / goal.target).coerceAtMost(1f)
                val newStreak = goal.streakDays + 1
                
                state.copy(
                    goal = goal.copy(
                        saved = newSaved,
                        progress = newProgress,
                        streakDays = newStreak
                    )
                )
            } ?: state
        }
    }
    
    fun revealScratchCard() {
        // Randomize reward for demo
        val rewards = listOf(
            "₹10 Cashback credited!",
            "₹5 Mobile Recharge coupon",
            "Free Chai voucher at Tapri!",
            "₹20 Fuel discount coupon"
        )
        
        _uiState.update {
            it.copy(
                scratchCardRevealed = true,
                reward = rewards.random()
            )
        }
    }
    
    fun resetScratchCard() {
        _uiState.update {
            it.copy(scratchCardRevealed = false, reward = "")
        }
    }
}
