package com.example.wuganjizhang.di

import android.content.Context
import androidx.room.Room
import com.example.wuganjizhang.data.local.AppDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideTransactionDao(database: AppDatabase) = database.transactionDao()
    
    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()
    
    @Provides
    fun provideAccountDao(database: AppDatabase) = database.accountDao()
    
    @Provides
    fun provideTagDao(database: AppDatabase) = database.tagDao()
    
    @Provides
    fun providePeriodicTemplateDao(database: AppDatabase) = database.periodicTemplateDao()
    
    @Provides
    fun provideCurrencyDao(database: AppDatabase) = database.currencyDao()
}
