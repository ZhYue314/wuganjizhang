package com.seamless.bookkeeper.di

import com.seamless.bookkeeper.data.repository.AccountRepositoryImpl
import com.seamless.bookkeeper.data.repository.CategoryRepositoryImpl
import com.seamless.bookkeeper.data.repository.TransactionRepositoryImpl
import com.seamless.bookkeeper.domain.repository.AccountRepository
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
