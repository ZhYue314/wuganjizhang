package com.example.wuganjizhang.data

import com.example.wuganjizhang.data.local.dao.AccountDao
import com.example.wuganjizhang.data.local.dao.CategoryDao
import com.example.wuganjizhang.model.Account
import com.example.wuganjizhang.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) {
    suspend fun initialize() = withContext(Dispatchers.IO) {
        // 检查是否已经初始化
        if (categoryDao.getCategoryCount() > 0 && accountDao.getAccountCount() > 0) {
            return@withContext
        }

        // 插入预设分类 - 支出
        val expenseCategories = listOf(
            Category(name = "餐饮", icon = "restaurant", color = "#FF6B6B", type = "expense", isPreset = true, order = 1),
            Category(name = "交通", icon = "directions_car", color = "#4ECDC4", type = "expense", isPreset = true, order = 2),
            Category(name = "购物", icon = "shopping_bag", color = "#95E1D3", type = "expense", isPreset = true, order = 3),
            Category(name = "娱乐", icon = "movie", color = "#F38181", type = "expense", isPreset = true, order = 4),
            Category(name = "住房", icon = "home", color = "#AA96DA", type = "expense", isPreset = true, order = 5),
            Category(name = "医疗", icon = "local_hospital", color = "#FCBAD3", type = "expense", isPreset = true, order = 6),
            Category(name = "教育", icon = "school", color = "#A8D8EA", type = "expense", isPreset = true, order = 7),
            Category(name = "其他", icon = "more_horiz", color = "#AAAAAA", type = "expense", isPreset = true, order = 8)
        )
        categoryDao.insertAll(expenseCategories)

        // 插入预设分类 - 收入
        val incomeCategories = listOf(
            Category(name = "工资", icon = "work", color = "#4CAF50", type = "income", isPreset = true, order = 1),
            Category(name = "奖金", icon = "card_giftcard", color = "#8BC34A", type = "income", isPreset = true, order = 2),
            Category(name = "理财", icon = "trending_up", color = "#CDDC39", type = "income", isPreset = true, order = 3),
            Category(name = "兼职", icon = "free_breakfast", color = "#FFEB3B", type = "income", isPreset = true, order = 4),
            Category(name = "其他", icon = "more_horiz", color = "#AAAAAA", type = "income", isPreset = true, order = 5)
        )
        categoryDao.insertAll(incomeCategories)

        // 插入默认账户
        val accounts = listOf(
            Account(name = "微信", type = "wechat", icon = "chat", color = "#07C160", balance = 0.0, initialBalance = 0.0, order = 1),
            Account(name = "支付宝", type = "alipay", icon = "payment", color = "#1677FF", balance = 0.0, initialBalance = 0.0, order = 2),
            Account(name = "银行卡", type = "bank", icon = "credit_card", color = "#FF6B6B", balance = 0.0, initialBalance = 0.0, order = 3),
            Account(name = "现金", type = "cash", icon = "attach_money", color = "#FFA500", balance = 0.0, initialBalance = 0.0, order = 4)
        )
        accountDao.insertAll(accounts)
    }
}
