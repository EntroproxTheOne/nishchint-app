package com.nischint.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nischint.app.data.models.Transaction
import java.util.UUID

/**
 * Room Entity for Transaction data
 * Maps to the Transaction data model
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: String,  // "expense" or "income"
    val amount: Int,
    val category: String,
    val merchant: String,
    val isBusiness: Boolean = false,
    val timestamp: String = "",  // ISO 8601 format: "2024-11-28T14:30:00"
    val createdAt: Long = System.currentTimeMillis()  // For sorting
) {
    /**
     * Convert to domain model
     */
    fun toTransaction() = Transaction(
        id = id,
        type = type,
        amount = amount,
        category = category,
        merchant = merchant,
        isBusiness = isBusiness,
        timestamp = timestamp
    )
    
    companion object {
        /**
         * Convert from domain model
         */
        fun fromTransaction(transaction: Transaction) = TransactionEntity(
            id = transaction.id,
            type = transaction.type,
            amount = transaction.amount,
            category = transaction.category,
            merchant = transaction.merchant,
            isBusiness = transaction.isBusiness,
            timestamp = transaction.timestamp.ifEmpty { 
                java.time.Instant.now().toString() 
            }
        )
    }
}

