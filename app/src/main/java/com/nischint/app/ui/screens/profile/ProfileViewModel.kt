package com.nischint.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.data.database.UserEntity
import com.nischint.app.data.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false
)

class ProfileViewModel(
    private val database: AppDatabase
) : ViewModel() {
    
    private val userDao = database.userDao()
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUser()
    }
    
    /**
     * Load current user from database
     */
    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                userDao.getCurrentUser()
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load user"
                            )
                        }
                    }
                    .collect { userEntity ->
                        _uiState.update {
                            it.copy(
                                user = userEntity?.toUser(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load user"
                    )
                }
            }
        }
    }
    
    /**
     * Update user profile
     */
    fun updateUser(name: String, phone: String, language: String) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.user
                if (currentUser != null) {
                    val updatedEntity = UserEntity(
                        id = currentUser.id,
                        name = name,
                        phone = phone,
                        isOnboarded = currentUser.isOnboarded,
                        language = language
                    )
                    userDao.upsertUser(updatedEntity)
                    _uiState.update { it.copy(isEditing = false) }
                } else {
                    // Create new user if none exists
                    val newUser = UserEntity(
                        id = "user_${System.currentTimeMillis()}",
                        name = name,
                        phone = phone,
                        isOnboarded = false,
                        language = language
                    )
                    userDao.upsertUser(newUser)
                    _uiState.update { it.copy(isEditing = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to update user")
                }
            }
        }
    }
    
    /**
     * Toggle edit mode
     */
    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

