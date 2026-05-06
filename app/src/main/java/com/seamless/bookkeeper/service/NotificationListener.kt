package com.seamless.bookkeeper.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.seamless.bookkeeper.service.parser.AlipayParser
import com.seamless.bookkeeper.service.parser.WeChatParser

class NotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "NotificationListener"
        private val MONITORED_PACKAGES = setOf(
            "com.tencent.mm",
            "com.eg.android.AlipayGphone"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName !in MONITORED_PACKAGES) return

        val title = sbn.notification.extras.getString(android.app.Notification.EXTRA_TITLE, "")
        val text = sbn.notification.extras.getString(android.app.Notification.EXTRA_TEXT, "")

        val transactionData = when (packageName) {
            "com.tencent.mm" -> WeChatParser.parse(title, text)
            "com.eg.android.AlipayGphone" -> AlipayParser.parse(title, text)
            else -> null
        }

        if (transactionData != null) {
            Log.d(TAG, "Detected transaction: $transactionData")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}

    override fun onListenerConnected() {
        Log.d(TAG, "Notification listener connected")
    }
}
