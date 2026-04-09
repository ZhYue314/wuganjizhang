package com.example.wuganjizhang.di

import android.content.Context
import androidx.room.Room
import com.example.wuganjizhang.data.local.AppDatabase
import com.example.wuganjizhang.data.local.dao.AccountDao
import com.example.wuganjizhang.data.local.dao.CategoryDao
import com.example.wuganjizhang.data.local.dao.TransactionDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bookkeeper.db"
        )
        .fallbackToDestructiveMigration() // 开发阶段使用，生产环境需要 proper migration
        .build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao {
        return database.accountDao()
    }
}
