package com.example.wuganjizhang.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wuganjizhang.data.local.converter.DateConverter
import com.example.wuganjizhang.data.local.converter.ListConverter
import com.example.wuganjizhang.data.local.dao.AccountDao
import com.example.wuganjizhang.data.local.dao.CategoryDao
import com.example.wuganjizhang.data.local.dao.TransactionDao
import com.example.wuganjizhang.model.Account
import com.example.wuganjizhang.model.Category
import com.example.wuganjizhang.model.Transaction

@Database(
    entities = [
        Transaction::class,
        Category::class,
        Account::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
}
