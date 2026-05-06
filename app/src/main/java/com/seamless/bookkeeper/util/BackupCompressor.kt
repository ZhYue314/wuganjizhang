package com.seamless.bookkeeper.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object BackupCompressor {
    fun compress(sourceFile: File): File {
        val compressedFile = File(sourceFile.parent, "${sourceFile.name}.gz")
        GZIPOutputStream(FileOutputStream(compressedFile)).use { gzip ->
            FileInputStream(sourceFile).use { input -> input.copyTo(gzip) }
        }
        return compressedFile
    }

    fun decompress(compressedFile: File, outputFile: File) {
        GZIPInputStream(FileInputStream(compressedFile)).use { gzip ->
            FileOutputStream(outputFile).use { output -> gzip.copyTo(output) }
        }
    }
}
