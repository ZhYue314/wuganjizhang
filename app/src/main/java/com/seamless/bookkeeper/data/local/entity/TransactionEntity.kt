package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: BigDecimal,
    @ColumnInfo(name = "original_currency") val originalCurrency: String = "CNY",
    @ColumnInfo(name = "original_amount") val originalAmount: BigDecimal? = null,
    @ColumnInfo(name = "exchange_rate") val exchangeRate: BigDecimal = BigDecimal.ONE,
    val type: String,
    @ColumnInfo(name = "category_id") val categoryId: Long? = null,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "to_account_id") val toAccountId: Long? = null,
    val timestamp: Long,
    @ColumnInfo(name = "merchant_name") val merchantName: String? = null,
    val note: String? = null,
    val source: String,
    val confidence: Float = 1.0f,
    @ColumnInfo(name = "is_refund") val isRefund: Boolean = false,
    @ColumnInfo(name = "original_transaction_id") val originalTransactionId: Long? = null,
    @ColumnInfo(name = "audio_uri") val audioUri: String? = null,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
