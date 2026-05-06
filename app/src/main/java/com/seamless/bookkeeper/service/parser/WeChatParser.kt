package com.seamless.bookkeeper.service.parser

import com.seamless.bookkeeper.service.TransactionData
import java.math.BigDecimal

object WeChatParser {

    private val paymentTitles = listOf("微信支付", "付款通知", "收款通知")
    private val amountPattern = Regex("""[¥￥]([\d,]+\.?\d*)""")
    private val merchantPattern = Regex("""收款方[：:]\s*(.+?)(?:\n|$)""")

    fun parse(title: String, text: String): TransactionData? {
        if (paymentTitles.none { title.contains(it) }) return null

        val amountMatch = amountPattern.find(text) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toBigDecimalOrNull() ?: return null

        val merchantMatch = merchantPattern.find(text)
        val merchant = merchantMatch?.groupValues?.get(1)?.trim()

        val type = if (text.contains("收款") || text.contains("转入")) "INCOME" else "EXPENSE"
        val isRefund = text.contains("退款") || title.contains("退款")

        return TransactionData(
            amount = amount,
            merchantName = merchant,
            type = type,
            isRefund = isRefund,
            source = "WECHAT",
            timestamp = System.currentTimeMillis()
        )
    }
}
