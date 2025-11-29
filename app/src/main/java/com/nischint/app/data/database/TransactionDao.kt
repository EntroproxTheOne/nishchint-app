package com.nischint.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Transaction operations
 */
@Dao
interface TransactionDao {
    /**
     * Get all transactions as Flow (reactive)
     */
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions by type (expense/income)
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY createdAt DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions by category
     */
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY createdAt DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions by business flag
     */
    @Query("SELECT * FROM transactions WHERE isBusiness = :isBusiness ORDER BY createdAt DESC")
    fun getTransactionsByBusiness(isBusiness: Boolean): Flow<List<TransactionEntity>>
    
    /**
     * Get transactions within date range
     */
    @Query("SELECT * FROM transactions WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    fun getTransactionsByDateRange(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>
    
    /**
     * Get transaction by ID
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?
    
    /**
     * Get total expense amount
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    fun getTotalExpense(): Flow<Int?>
    
    /**
     * Get total income amount
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income'")
    fun getTotalIncome(): Flow<Int?>
    
    /**
     * Get total expense by category
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND category = :category")
    suspend fun getExpenseByCategory(category: String): Int?
    
    /**
     * Insert or update transaction
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(transaction: TransactionEntity)
    
    /**
     * Insert multiple transactions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    
    /**
     * Update transaction
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    /**
     * Delete transaction
     */
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    /**
     * Delete transaction by ID
     */
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: String)
    
    /**
     * Delete all transactions
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    /**
     * Get transaction count
     */
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
    
    /**
     * Check if transaction exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE id = :transactionId)")
    suspend fun transactionExists(transactionId: String): Boolean
}

