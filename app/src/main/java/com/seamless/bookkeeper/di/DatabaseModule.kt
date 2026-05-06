package com.seamless.bookkeeper.di

import android.content.Context
import androidx.room.Room
import com.seamless.bookkeeper.data.local.AppDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
    @Provides fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides fun providePeriodicTemplateDao(db: AppDatabase): PeriodicTemplateDao = db.periodicTemplateDao()
    @Provides fun provideBackupRecordDao(db: AppDatabase): BackupRecordDao = db.backupRecordDao()
    @Provides fun provideCurrencyRateDao(db: AppDatabase): CurrencyRateDao = db.currencyRateDao()
    @Provides fun provideArchiveRecordDao(db: AppDatabase): ArchiveRecordDao = db.archiveRecordDao()
    @Provides fun provideSmartRuleDao(db: AppDatabase): SmartRuleDao = db.smartRuleDao()
    @Provides fun providePendingTransactionDao(db: AppDatabase): PendingTransactionDao = db.pendingTransactionDao()
    @Provides fun provideTransactionImageDao(db: AppDatabase): TransactionImageDao = db.transactionImageDao()
    @Provides fun provideTemplateSkipDateDao(db: AppDatabase): TemplateSkipDateDao = db.templateSkipDateDao()
}
