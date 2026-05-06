package com.seamless.bookkeeper.util

import android.content.Context
import java.io.File

class CrashReporter(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        saveCrashReport(throwable)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashReport(throwable: Throwable) {
        try {
            val crashDir = File(context.filesDir, "crash_logs").apply { mkdirs() }
            val report = buildString {
                appendLine("Time: ${System.currentTimeMillis()}")
                appendLine("Exception: ${throwable.javaClass.name}")
                appendLine("Message: ${throwable.message}")
                appendLine("Stack: ${throwable.stackTraceToString()}")
            }
            File(crashDir, "crash_${System.currentTimeMillis()}.log").writeText(report)
        } catch (_: Exception) { }
    }
}
