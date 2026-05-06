package com.seamless.bookkeeper.domain.model

import java.math.BigDecimal

data class Transaction(
    val id: Long = 0,
    val amount: BigDecimal,
    val originalCurrency: String = "CNY",
    val originalAmount: BigDecimal? = null,
    val exchangeRate: BigDecimal = BigDecimal.ONE,
    val type: TransactionType,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categoryColor: Int? = null,
    val accountId: Long,
    val accountName: String = "",
    val toAccountId: Long? = null,
    val timestamp: Long,
    val merchantName: String? = null,
    val note: String? = null,
    val source: TransactionSource,
    val confidence: Float = 1.0f,
    val isRefund: Boolean = false,
    val originalTransactionId: Long? = null,
    val tags: List<String> = emptyList(),
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

enum class TransactionType { EXPENSE, INCOME, TRANSFER }
enum class TransactionSource { AUTO, MANUAL, PERIODIC, FUSED }
