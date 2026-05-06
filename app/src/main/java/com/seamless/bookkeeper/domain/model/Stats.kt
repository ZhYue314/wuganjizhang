package com.seamless.bookkeeper.domain.model

import java.math.BigDecimal

data class MonthlyStats(
    val period: Int,
    val totalExpense: BigDecimal,
    val totalIncome: BigDecimal,
    val transactionCount: Int
)

data class CategoryBreakdown(
    val categoryName: String,
    val categoryColor: Int,
    val totalAmount: BigDecimal,
    val transactionCount: Int,
    val percentage: Float = 0f
)

data class DailyStats(
    val date: String,
    val totalExpense: BigDecimal,
    val totalIncome: BigDecimal
)
