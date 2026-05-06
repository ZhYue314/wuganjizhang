package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo

data class MerchantCategoryStat(
    @ColumnInfo(name = "merchant_name") val merchantName: String,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "cnt") val count: Int
) {
    val confidence: Float get() = (count / 10f).coerceAtMost(1.0f)
}
