package com.seamless.bookkeeper.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BookkeeperAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "AccessibilityService"
        private val MONITORED_PACKAGES = setOf(
            "com.tencent.mm",
            "com.eg.android.AlipayGphone"
        )
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = MONITORED_PACKAGES.toTypedArray()
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString() ?: return
                if (packageName in MONITORED_PACKAGES) {
                    val text = extractText(event.source)
                    Log.d(TAG, "Window content: $text")
                }
            }
        }
    }

    private fun extractText(source: AccessibilityNodeInfo?): String {
        source ?: return ""
        val sb = StringBuilder()
        if (source.text != null) sb.append(source.text).append(" ")
        for (i in 0 until source.childCount) {
            sb.append(extractText(source.getChild(i)))
        }
        return sb.toString().trim()
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }
}
