package com.seamless.bookkeeper.domain.model

import java.math.BigDecimal

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val initialBalance: BigDecimal,
    val currentBalance: BigDecimal,
    val icon: String,
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0
)

enum class AccountType { WECHAT, ALIPAY, BANK_CARD, CASH, OTHER }
