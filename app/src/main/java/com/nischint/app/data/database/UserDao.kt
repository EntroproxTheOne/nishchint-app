package com.nischint.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations
 */
@Dao
interface UserDao {
    /**
     * Get user by ID as Flow (reactive)
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>
    
    /**
     * Get current user (assuming single user app)
     * Returns the first user in the database
     */
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>
    
    /**
     * Insert or update user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)
    
    /**
     * Update user
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Delete user
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * Delete all users
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    /**
     * Check if user exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :userId)")
    suspend fun userExists(userId: String): Boolean
}

