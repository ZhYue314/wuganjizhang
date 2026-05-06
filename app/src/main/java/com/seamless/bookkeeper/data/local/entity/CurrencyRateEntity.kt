package com.seamless.bookkeeper.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "currency_rates",
    indices = [Index(value = ["currency_code"], unique = true)]
)
data class CurrencyRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    @ColumnInfo(name = "currency_name") val currencyName: String,
    val symbol: String,
    @ColumnInfo(name = "rate_to_cny") val rateToCny: BigDecimal,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
