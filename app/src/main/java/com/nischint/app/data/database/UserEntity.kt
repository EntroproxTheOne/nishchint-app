package com.nischint.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for User profile data
 * Maps to the User data model
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val isOnboarded: Boolean = false,
    val language: String = "hi"  // Hindi default
) {
    /**
     * Convert to domain model
     */
    fun toUser() = com.nischint.app.data.models.User(
        id = id,
        name = name,
        phone = phone,
        isOnboarded = isOnboarded,
        language = language
    )
    
    companion object {
        /**
         * Convert from domain model
         */
        fun fromUser(user: com.nischint.app.data.models.User) = UserEntity(
            id = user.id,
            name = user.name,
            phone = user.phone,
            isOnboarded = user.isOnboarded,
            language = user.language
        )
    }
}

