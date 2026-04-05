package com.example.wuganjizhang.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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
 * Accessibility Service for detecting payment completion screens in WeChat and Alipay
 */
@AndroidEntryPoint
class PaymentAccessibilityService : AccessibilityService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    @Inject
    lateinit var transactionRepository: TransactionRepository
    
    companion object {
        private const val TAG = "PaymentAccessibility"
        
        private const val WECHAT_PACKAGE = "com.tencent.mm"
        private const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val packageName = event.packageName?.toString() ?: return
        
        // Only process WeChat and Alipay events
        if (packageName != WECHAT_PACKAGE && packageName != ALIPAY_PACKAGE) {
            return
        }
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event, packageName)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                handleWindowContentChanged(event, packageName)
            }
        }
    }
    
    private fun handleWindowStateChanged(event: AccessibilityEvent, packageName: String) {
        val className = event.className?.toString() ?: return
        
        Log.d(TAG, "Window state changed: $className in $packageName")
        
        // Check for payment success activities/screens
        if (isPaymentSuccessScreen(className, packageName)) {
            serviceScope.launch {
                try {
                    val transaction = extractTransactionFromScreen(packageName)
                    if (transaction != null) {
                        transactionRepository.insertTransaction(transaction)
                        Log.d(TAG, "Transaction saved from accessibility: $transaction")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error extracting transaction", e)
                }
            }
        }
    }
    
    private fun handleWindowContentChanged(event: AccessibilityEvent, packageName: String) {
        // Monitor content changes for payment confirmation
        val root = rootInActiveWindow ?: return
        
        try {
            val paymentText = findPaymentConfirmationText(root)
            if (paymentText != null) {
                Log.d(TAG, "Found payment confirmation: $paymentText")
                // Process the payment information
            }
        } finally {
            root.recycle()
        }
    }
    
    /**
     * Check if current screen is a payment success screen
     */
    private fun isPaymentSuccessScreen(className: String, packageName: String): Boolean {
        // These class names are examples and need to be verified with actual apps
        val successIndicators = listOf("Success", "Result", "Complete")
        
        return successIndicators.any { className.contains(it, ignoreCase = true) }
    }
    
    /**
     * Extract transaction details from the current screen
     */
    private suspend fun extractTransactionFromScreen(packageName: String): Transaction? {
        val root = rootInActiveWindow ?: return null
        
        return try {
            // Find amount text
            val amount = findAmountInNode(root)
            
            // Find merchant name
            val merchant = findMerchantInNode(root)
            
            if (amount == null || amount <= 0) {
                return null
            }
            
            Transaction(
                amount = amount,
                originalCurrency = "CNY",
                originalAmount = amount,
                exchangeRate = 1.0,
                type = TransactionType.EXPENSE,
                categoryId = null,
                accountId = 1, // Default account
                merchant = merchant,
                timestamp = Date(),
                source = TransactionSource.AUTO_DETECT,
                confidence = 0.9f // Higher confidence from accessibility service
            )
        } finally {
            root.recycle()
        }
    }
    
    /**
     * Find payment confirmation text in the view hierarchy
     */
    private fun findPaymentConfirmationText(node: AccessibilityNodeInfo): String? {
        val paymentKeywords = listOf("付款成功", "支付成功", "Payment Successful")
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            
            try {
                val text = child.text?.toString()
                if (text != null && paymentKeywords.any { text.contains(it) }) {
                    return text
                }
                
                val result = findPaymentConfirmationText(child)
                if (result != null) return result
            } finally {
                child.recycle()
            }
        }
        
        return null
    }
    
    /**
     * Find amount in the view hierarchy
     */
    private fun findAmountInNode(node: AccessibilityNodeInfo): Double? {
        val amountRegex = Regex("[¥$]([\\d,]+\\.?\\d*)")
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            
            try {
                val text = child.text?.toString()
                if (text != null) {
                    val match = amountRegex.find(text)
                    val amount = match?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        return amount
                    }
                }
                
                val result = findAmountInNode(child)
                if (result != null) return result
            } finally {
                child.recycle()
            }
        }
        
        return null
    }
    
    /**
     * Find merchant name in the view hierarchy
     */
    private fun findMerchantInNode(node: AccessibilityNodeInfo): String? {
        // Look for text that might be a merchant name
        // This is simplified and needs refinement
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            
            try {
                val text = child.text?.toString()
                val viewId = child.viewIdResourceName ?: ""
                
                // Look for merchant-related view IDs or text patterns
                if (viewId.contains("merchant", ignoreCase = true) || 
                    viewId.contains("shop", ignoreCase = true)) {
                    return text
                }
                
                val result = findMerchantInNode(child)
                if (result != null) return result
            } finally {
                child.recycle()
            }
        }
        
        return null
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility service connected")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Accessibility service destroyed")
    }
}
