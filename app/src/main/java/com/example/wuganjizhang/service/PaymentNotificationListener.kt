package com.example.wuganjizhang.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.wuganjizhang.data.model.Transaction
import com.example.wuganjizhang.data.model.TransactionSource
import com.example.wuganjizhang.data.model.TransactionType
import com.example.wuganjizhang.data.repository.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Notification Listener Service for detecting payment notifications from WeChat and Alipay
 */
@AndroidEntryPoint
class PaymentNotificationListener : NotificationListenerService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    @Inject
    lateinit var transactionRepository: TransactionRepository
    
    companion object {
        private const val TAG = "PaymentNotification"
        
        // WeChat package name
        private const val WECHAT_PACKAGE = "com.tencent.mm"
        
        // Alipay package name
        private const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        if (sbn == null) return
        
        val packageName = sbn.packageName
        
        // Only process WeChat and Alipay notifications
        if (packageName != WECHAT_PACKAGE && packageName != ALIPAY_PACKAGE) {
            return
        }
        
        val notification = sbn.notification
        val extras = notification.extras
        
        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        
        Log.d(TAG, "Notification from $packageName: $title - $text")
        
        // Check if this is a payment notification
        if (isPaymentNotification(title, text, packageName)) {
            serviceScope.launch {
                try {
                    val transaction = parsePaymentNotification(title, text, packageName)
                    if (transaction != null) {
                        transactionRepository.insertTransaction(transaction)
                        Log.d(TAG, "Transaction saved: $transaction")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing notification", e)
                }
            }
        }
    }
    
    /**
     * Check if the notification is a payment notification
     */
    private fun isPaymentNotification(title: String, text: String, packageName: String): Boolean {
        val paymentKeywords = listOf("付款成功", "支付成功", "收款成功", "转账成功", "payment successful")
        
        val combinedText = "$title $text".lowercase()
        return paymentKeywords.any { it in combinedText }
    }
    
    /**
     * Parse payment notification and extract transaction details
     * This is a simplified implementation - needs enhancement based on actual notification formats
     */
    private suspend fun parsePaymentNotification(
        title: String, 
        text: String, 
        packageName: String
    ): Transaction? {
        // TODO: Implement robust parsing logic
        // This is a placeholder that needs to be refined based on actual notification formats
        
        return try {
            // Extract amount using regex (simplified)
            val amountRegex = Regex("[¥$]([\\d,]+\\.?\\d*)")
            val amountMatch = amountRegex.find(text)
            val amount = amountMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            
            if (amount == null || amount <= 0) {
                Log.w(TAG, "Could not extract valid amount from notification")
                return null
            }
            
            // Determine transaction type (expense or income)
            val isIncome = text.contains("收款") || text.contains("received")
            val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
            
            // Extract merchant name (simplified)
            val merchant = extractMerchantName(title, text)
            
            Transaction(
                amount = amount,
                originalCurrency = "CNY",
                originalAmount = amount,
                exchangeRate = 1.0,
                type = type,
                categoryId = null, // Will be auto-assigned later based on learning
                accountId = 1, // Default account - should be fetched from settings
                merchant = merchant,
                timestamp = Date(),
                source = TransactionSource.AUTO_DETECT,
                confidence = 0.8f // Initial confidence score
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing notification", e)
            null
        }
    }
    
    /**
     * Extract merchant name from notification
     */
    private fun extractMerchantName(title: String, text: String): String? {
        // Simplified extraction - needs refinement
        return if (title.isNotEmpty() && title != "微信支付" && title != "支付宝") {
            title
        } else {
            null
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
