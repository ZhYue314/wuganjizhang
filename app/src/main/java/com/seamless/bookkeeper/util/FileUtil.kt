package com.seamless.bookkeeper.util

import android.content.Context
import java.io.File

object FileUtil {
    fun getBackupDir(context: Context): File {
        return File(context.filesDir, "backups").also { it.mkdirs() }
    }

    fun getCacheSize(context: Context): Long {
        return context.cacheDir.walkTopDown().sumOf { it.length() } +
                File(context.filesDir, "image_cache").walkTopDown().sumOf { it.length() }
    }

    fun clearCache(context: Context): Boolean {
        return context.cacheDir.deleteRecursively().also {
            context.cacheDir.mkdirs()
        }
    }

    fun getFileSize(path: String): Long {
        return File(path).let { if (it.exists()) it.length() else 0L }
    }
}
