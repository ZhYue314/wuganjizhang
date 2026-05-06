package com.seamless.bookkeeper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seamless.bookkeeper.data.local.converter.BigDecimalConverter
import com.seamless.bookkeeper.data.local.converter.DateConverter
import com.seamless.bookkeeper.data.local.converter.ListConverter
import com.seamless.bookkeeper.data.local.dao.AccountDao
import com.seamless.bookkeeper.data.local.dao.ArchiveRecordDao
import com.seamless.bookkeeper.data.local.dao.BackupRecordDao
import com.seamless.bookkeeper.data.local.dao.CategoryDao
import com.seamless.bookkeeper.data.local.dao.CurrencyRateDao
import com.seamless.bookkeeper.data.local.dao.PendingTransactionDao
import com.seamless.bookkeeper.data.local.dao.PeriodicTemplateDao
import com.seamless.bookkeeper.data.local.dao.SmartRuleDao
import com.seamless.bookkeeper.data.local.dao.TagDao
import com.seamless.bookkeeper.data.local.dao.TemplateSkipDateDao
import com.seamless.bookkeeper.data.local.dao.TransactionDao
import com.seamless.bookkeeper.data.local.dao.TransactionImageDao
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.data.local.entity.ArchiveRecordEntity
import com.seamless.bookkeeper.data.local.entity.BackupRecordEntity
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import com.seamless.bookkeeper.data.local.entity.CurrencyRateEntity
import com.seamless.bookkeeper.data.local.entity.PendingTransactionEntity
import com.seamless.bookkeeper.data.local.entity.PeriodicTemplateEntity
import com.seamless.bookkeeper.data.local.entity.SmartRuleEntity
import com.seamless.bookkeeper.data.local.entity.TagEntity
import com.seamless.bookkeeper.data.local.entity.TemplateSkipDateEntity
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.entity.TransactionImageEntity
import com.seamless.bookkeeper.data.local.entity.TransactionTagCrossRef

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        AccountEntity::class,
        PeriodicTemplateEntity::class,
        BackupRecordEntity::class,
        CurrencyRateEntity::class,
        ArchiveRecordEntity::class,
        SmartRuleEntity::class,
        PendingTransactionEntity::class,
        TransactionImageEntity::class,
        TemplateSkipDateEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    BigDecimalConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun accountDao(): AccountDao
    abstract fun periodicTemplateDao(): PeriodicTemplateDao
    abstract fun backupRecordDao(): BackupRecordDao
    abstract fun currencyRateDao(): CurrencyRateDao
    abstract fun archiveRecordDao(): ArchiveRecordDao
    abstract fun smartRuleDao(): SmartRuleDao
    abstract fun pendingTransactionDao(): PendingTransactionDao
    abstract fun transactionImageDao(): TransactionImageDao
    abstract fun templateSkipDateDao(): TemplateSkipDateDao

    companion object {
        const val DATABASE_NAME = "bookkeeper.db"
    }
}
