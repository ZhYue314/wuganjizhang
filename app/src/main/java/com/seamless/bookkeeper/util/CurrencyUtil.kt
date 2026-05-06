package com.seamless.bookkeeper.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object CurrencyUtil {
    private val cnyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    fun formatCNY(amount: BigDecimal): String {
        return "¥${amount.setScale(2, RoundingMode.HALF_UP)}"
    }

    fun formatAmount(amount: BigDecimal, currencyCode: String = "CNY"): String {
        return when (currencyCode) {
            "CNY" -> formatCNY(amount)
            "USD" -> "$${amount.setScale(2, RoundingMode.HALF_UP)}"
            "EUR" -> "€${amount.setScale(2, RoundingMode.HALF_UP)}"
            "JPY" -> "¥${amount.setScale(0, RoundingMode.HALF_UP)}"
            else -> "${amount.setScale(2, RoundingMode.HALF_UP)} $currencyCode"
        }
    }

    fun parseAmount(text: String): BigDecimal? {
        val cleaned = text.replace(",", "").replace("¥", "").replace("$", "").trim()
        return cleaned.toBigDecimalOrNull()
    }
}
