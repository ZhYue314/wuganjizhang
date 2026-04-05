package com.example.wuganjizhang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AccountType, // WECHAT, ALIPAY, BANK_CARD, CASH, etc.
    val initialBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val icon: String? = null,
    val color: String? = null,
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true,
    val createdAt: java.util.Date = java.util.Date()
)

enum class AccountType {
    WECHAT, ALIPAY, BANK_CARD, CASH, OTHER
}
