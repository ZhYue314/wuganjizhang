package com.example.wuganjizhang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val currencyCode: String, // e.g., "USD", "EUR"
    val currencyName: String,
    val exchangeRate: Double, // Rate to CNY
    val updatedAt: Date = Date(),
    val symbol: String = "$"
)
