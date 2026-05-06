package com.seamless.bookkeeper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageCompressor {
    private const val MAX_DIMENSION = 2048
    private const val MAX_SIZE_KB = 500

    fun compress(context: Context, imageUri: Uri): ByteArray {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return ByteArray(0)

        options.inSampleSize = computeSampleSize(options.outWidth, options.outHeight)
        options.inJustDecodeBounds = false

        val bitmap = context.contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return ByteArray(0)

        return ByteArrayOutputStream().use { output ->
            var quality = 90
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, output)
            while (output.size() > MAX_SIZE_KB * 1024 && quality > 10) {
                output.reset()
                quality -= 10
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, output)
            }
            bitmap.recycle()
            output.toByteArray()
        }
    }

    private fun computeSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while (width / sampleSize > MAX_DIMENSION || height / sampleSize > MAX_DIMENSION) {
            sampleSize *= 2
        }
        return sampleSize
    }
}
