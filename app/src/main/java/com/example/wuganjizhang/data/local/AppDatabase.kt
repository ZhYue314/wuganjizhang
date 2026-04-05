package com.example.wuganjizhang.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wuganjizhang.data.dao.*
import com.example.wuganjizhang.data.model.*

@Database(
    entities = [
        Transaction::class,
        Category::class,
        Account::class,
        Tag::class,
        PeriodicTemplate::class,
        Currency::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun tagDao(): TagDao
    abstract fun periodicTemplateDao(): PeriodicTemplateDao
    abstract fun currencyDao(): CurrencyDao
    
    companion object {
        const val DATABASE_NAME = "wuganjizhang.db"
    }
}
