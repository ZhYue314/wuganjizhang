package com.seamless.bookkeeper.data

import com.seamless.bookkeeper.data.local.dao.AccountDao
import com.seamless.bookkeeper.data.local.dao.CategoryDao
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) {
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (categoryDao.getCount() > 0 && accountDao.getCount() > 0) return@withContext

        val expenseCategories = listOf(
            CategoryEntity(name = "餐饮", icon = "\uD83C\uDF5C", color = 0xFFED9B40.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 1),
            CategoryEntity(name = "交通", icon = "\uD83D\uDE97", color = 0xFF5E9BD5.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 2),
            CategoryEntity(name = "购物", icon = "\uD83D\uDED2", color = 0xFF4CAF7C.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 3),
            CategoryEntity(name = "娱乐", icon = "\uD83C\uDFAE", color = 0xFFA779D4.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 4),
            CategoryEntity(name = "住房", icon = "\uD83C\uDFE0", color = 0xFF9C8470.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 5),
            CategoryEntity(name = "医疗", icon = "\uD83D\uDC8A", color = 0xFFE8726A.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 6),
            CategoryEntity(name = "教育", icon = "\uD83D\uDCDA", color = 0xFF54C4B0.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 7),
            CategoryEntity(name = "通讯", icon = "\uD83D\uDCF1", color = 0xFF8499A5.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 8),
            CategoryEntity(name = "社交", icon = "\uD83D\uDC65", color = 0xFFE07DA0.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 9),
            CategoryEntity(name = "办公", icon = "\uD83D\uDCBC", color = 0xFF6E8CC4.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 10),
            CategoryEntity(name = "旅行", icon = "\u2708\uFE0F", color = 0xFF4EB8AE.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 11),
            CategoryEntity(name = "其他", icon = "\uD83D\uDCDD", color = 0xFF97A597.toInt(), type = "EXPENSE", isPreset = true, sortOrder = 12)
        )
        categoryDao.insertAll(expenseCategories)

        val incomeCategories = listOf(
            CategoryEntity(name = "工资", icon = "\uD83D\uDCB0", color = 0xFF4CAF7C.toInt(), type = "INCOME", isPreset = true, sortOrder = 1),
            CategoryEntity(name = "奖金", icon = "\uD83C\uDFC6", color = 0xFF5E9BD5.toInt(), type = "INCOME", isPreset = true, sortOrder = 2),
            CategoryEntity(name = "理财", icon = "\uD83D\uDCC8", color = 0xFFA779D4.toInt(), type = "INCOME", isPreset = true, sortOrder = 3),
            CategoryEntity(name = "兼职", icon = "\uD83D\uDCBC", color = 0xFFED9B40.toInt(), type = "INCOME", isPreset = true, sortOrder = 4),
            CategoryEntity(name = "其他收入", icon = "\uD83D\uDCB5", color = 0xFF97A597.toInt(), type = "INCOME", isPreset = true, sortOrder = 5)
        )
        categoryDao.insertAll(incomeCategories)

        val accounts = listOf(
            AccountEntity(name = "微信", type = "WECHAT", icon = "\uD83D\uDCB3", initialBalance = BigDecimal.ZERO, currentBalance = BigDecimal.ZERO, sortOrder = 1),
            AccountEntity(name = "支付宝", type = "ALIPAY", icon = "\uD83D\uDCB3", initialBalance = BigDecimal.ZERO, currentBalance = BigDecimal.ZERO, sortOrder = 2),
            AccountEntity(name = "银行卡", type = "BANK_CARD", icon = "\uD83C\uDFE6", initialBalance = BigDecimal.ZERO, currentBalance = BigDecimal.ZERO, sortOrder = 3),
            AccountEntity(name = "现金", type = "CASH", icon = "\uD83D\uDCB5", initialBalance = BigDecimal.ZERO, currentBalance = BigDecimal.ZERO, sortOrder = 4)
        )
        accountDao.insertAll(accounts)
    }
}
