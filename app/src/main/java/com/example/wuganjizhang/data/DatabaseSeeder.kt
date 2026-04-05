package com.example.wuganjizhang.data

import com.example.wuganjizhang.data.local.AppDatabase
import com.example.wuganjizhang.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

object DatabaseSeeder {
    
    fun seed(database: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            seedCategories(database)
            seedAccounts(database)
            seedCurrencies(database)
        }
    }
    
    private suspend fun seedCategories(database: AppDatabase) {
        val categoryDao = database.categoryDao()
        
        // Check if already seeded
        if (categoryDao.getCustomCategoryCount() > 0 || 
            categoryDao.getAllEnabledCategories().value.isNotEmpty()) {
            return
        }
        
        val expenseCategories = listOf(
            Category(name = "餐饮", icon = "🍜", color = "#FF9500", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 1),
            Category(name = "交通", icon = "🚌", color = "#5856D6", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 2),
            Category(name = "购物", icon = "🛒", color = "#34C759", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 3),
            Category(name = "娱乐", icon = "🎮", color = "#FF3B30", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 4),
            Category(name = "医疗", icon = "💊", color = "#FF2D55", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 5),
            Category(name = "教育", icon = "📚", color = "#5AC8FA", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 6),
            Category(name = "住房", icon = "🏠", color = "#4F6EF7", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 7),
            Category(name = "通讯", icon = "📱", color = "#AF52DE", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 8),
            Category(name = "服饰", icon = "👔", color = "#FF6482", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 9),
            Category(name = "社交", icon = "👥", color = "#FF9F0A", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 10),
            Category(name = "宠物", icon = "🐾", color = "#64D2FF", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 11),
            Category(name = "其他", icon = "📦", color = "#9CA3AF", type = CategoryType.EXPENSE, isPreset = true, sortOrder = 12)
        )
        
        val incomeCategories = listOf(
            Category(name = "工资", icon = "💰", color = "#34C759", type = CategoryType.INCOME, isPreset = true, sortOrder = 1),
            Category(name = "奖金", icon = "🎁", color = "#FF9500", type = CategoryType.INCOME, isPreset = true, sortOrder = 2),
            Category(name = "投资", icon = "📈", color = "#5856D6", type = CategoryType.INCOME, isPreset = true, sortOrder = 3),
            Category(name = "兼职", icon = "💼", color = "#AF52DE", type = CategoryType.INCOME, isPreset = true, sortOrder = 4),
            Category(name = "其他收入", icon = "💵", color = "#9CA3AF", type = CategoryType.INCOME, isPreset = true, sortOrder = 5)
        )
        
        (expenseCategories + incomeCategories).forEach {
            categoryDao.insertCategory(it)
        }
    }
    
    private suspend fun seedAccounts(database: AppDatabase) {
        val accountDao = database.accountDao()
        
        // Check if already seeded
        if (accountDao.getAllAccounts().value.isNotEmpty()) {
            return
        }
        
        val accounts = listOf(
            Account(name = "微信钱包", type = AccountType.WECHAT, initialBalance = 0.0, currentBalance = 0.0, icon = "💬", color = "#07C160", isDefault = true),
            Account(name = "支付宝", type = AccountType.ALIPAY, initialBalance = 0.0, currentBalance = 0.0, icon = "🔵", color = "#1677FF"),
            Account(name = "招商银行卡", type = AccountType.BANK_CARD, initialBalance = 0.0, currentBalance = 0.0, icon = "🏦", color = "#C41230"),
            Account(name = "现金", type = AccountType.CASH, initialBalance = 0.0, currentBalance = 0.0, icon = "💵", color = "#F59E0B")
        )
        
        accounts.forEach {
            accountDao.insertAccount(it)
        }
        
        // Set first account as default
        accountDao.setDefaultAccount(accounts[0].id)
    }
    
    private suspend fun seedCurrencies(database: AppDatabase) {
        val currencyDao = database.currencyDao()
        
        // Check if already seeded
        if (currencyDao.getCurrencyByCode("USD") != null) {
            return
        }
        
        val currencies = listOf(
            Currency(currencyCode = "CNY", currencyName = "人民币", exchangeRate = 1.0, symbol = "¥"),
            Currency(currencyCode = "USD", currencyName = "美元", exchangeRate = 7.12, symbol = "$"),
            Currency(currencyCode = "EUR", currencyName = "欧元", exchangeRate = 7.85, symbol = "€"),
            Currency(currencyCode = "JPY", currencyName = "日元", exchangeRate = 0.0485, symbol = "¥"),
            Currency(currencyCode = "GBP", currencyName = "英镑", exchangeRate = 9.05, symbol = "£"),
            Currency(currencyCode = "HKD", currencyName = "港币", exchangeRate = 0.91, symbol = "HK$")
        )
        
        currencies.forEach {
            currencyDao.insertCurrency(it)
        }
    }
}
