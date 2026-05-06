package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TransactionWithRelations(
    @Embedded val transaction: TransactionEntity,
    @ColumnInfo(name = "category_name") val categoryName: String?,
    @ColumnInfo(name = "category_color") val categoryColor: Int?,
    @ColumnInfo(name = "account_name") val accountName: String
)
