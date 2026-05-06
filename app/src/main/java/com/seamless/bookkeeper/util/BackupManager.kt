package com.seamless.bookkeeper.util

import android.content.Context
import com.seamless.bookkeeper.data.local.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val backupDir = File(context.filesDir, "backups").apply { mkdirs() }

    suspend fun createBackup(): File? {
        return try {
            val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            val backupFile = File(backupDir, "backup_${System.currentTimeMillis()}.db.gz")

            GZIPOutputStream(FileOutputStream(backupFile)).use { gzip ->
                FileInputStream(dbFile).use { input -> input.copyTo(gzip) }
            }
            backupFile
        } catch (e: Exception) { null }
    }

    suspend fun restoreBackup(backupFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            val tempFile = File(backupDir, "restore_temp.db")
            GZIPInputStream(FileInputStream(backupFile)).use { gzip ->
                FileOutputStream(tempFile).use { output -> gzip.copyTo(output) }
            }
            tempFile.copyTo(dbFile, overwrite = true)
            tempFile.delete()
            true
        } catch (e: Exception) { false }
    }
}
