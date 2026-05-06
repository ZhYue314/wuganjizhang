package com.seamless.bookkeeper.service.parser

import com.seamless.bookkeeper.service.TransactionData
import java.math.BigDecimal

object AlipayParser {

    private val paymentTitles = listOf("支付宝", "付款通知", "交易提醒")
    private val amountPattern = Regex("""[¥￥]([\d,]+\.?\d*)""")
    private val merchantPattern = Regex("""商户[：:]\s*(.+?)(?:\n|$)""")

    fun parse(title: String, text: String): TransactionData? {
        if (paymentTitles.none { title.contains(it) }) return null

        val amountMatch = amountPattern.find(text) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toBigDecimalOrNull() ?: return null

        val merchantMatch = merchantPattern.find(text)
        val merchant = merchantMatch?.groupValues?.get(1)?.trim()

        val type = if (text.contains("收款") || text.contains("收入")) "INCOME" else "EXPENSE"
        val isRefund = text.contains("退款") || text.contains("已退款")

        return TransactionData(
            amount = amount,
            merchantName = merchant,
            type = type,
            isRefund = isRefund,
            source = "ALIPAY",
            timestamp = System.currentTimeMillis()
        )
    }
}
