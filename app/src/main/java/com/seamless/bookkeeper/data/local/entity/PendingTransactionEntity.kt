package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "pending_transactions",
    indices = [Index(value = ["created_at"])]
)
data class PendingTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: BigDecimal,
    @ColumnInfo(name = "original_currency") val originalCurrency: String = "CNY",
    @ColumnInfo(name = "original_amount") val originalAmount: BigDecimal? = null,
    @ColumnInfo(name = "exchange_rate") val exchangeRate: BigDecimal = BigDecimal.ONE,
    val type: String,
    @ColumnInfo(name = "suggested_category_id") val suggestedCategoryId: Long? = null,
    @ColumnInfo(name = "account_id") val accountId: Long,
    val timestamp: Long,
    @ColumnInfo(name = "merchant_name") val merchantName: String? = null,
    val source: String,
    val confidence: Float,
    @ColumnInfo(name = "is_refund") val isRefund: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "expires_at") val expiresAt: Long
)
