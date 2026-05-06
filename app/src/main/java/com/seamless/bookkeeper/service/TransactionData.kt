package com.seamless.bookkeeper.service

import java.math.BigDecimal

data class TransactionData(
    val amount: BigDecimal,
    val merchantName: String? = null,
    val type: String = "EXPENSE",
    val isRefund: Boolean = false,
    val source: String = "WECHAT",
    val timestamp: Long = System.currentTimeMillis(),
    val confidence: Float = 0.8f
) {
    val fusionKey: String get() = "$amount-$merchantName-${timestamp / 10000}"
}
