package com.example.wuganjizhang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val originalCurrency: String = "CNY",
    val originalAmount: Double = 0.0,
    val exchangeRate: Double = 1.0,
    val type: TransactionType, // EXPENSE, INCOME, TRANSFER
    val categoryId: Long?,
    val accountId: Long,
    val toAccountId: Long? = null, // For transfers
    val merchant: String? = null,
    val timestamp: Date,
    val note: String? = null,
    val tags: String? = null, // Comma-separated tag IDs
    val source: TransactionSource, // AUTO_DETECT, MANUAL, PERIODIC
    val confidence: Float = 1.0f, // For auto-detected transactions
    val hasImage: Boolean = false,
    val hasVoiceNote: Boolean = false,
    val isPeriodic: Boolean = false,
    val periodicTemplateId: Long? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TransactionType {
    EXPENSE, INCOME, TRANSFER
}

enum class TransactionSource {
    AUTO_DETECT, MANUAL, PERIODIC
}
