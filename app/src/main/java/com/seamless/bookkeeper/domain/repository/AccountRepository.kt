package com.seamless.bookkeeper.domain.repository

import com.seamless.bookkeeper.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface AccountRepository {
    suspend fun insert(account: AccountEntity): Long
    suspend fun update(account: AccountEntity): Int
    suspend fun delete(id: Long): Int
    suspend fun getById(id: Long): AccountEntity?
    suspend fun getAllEnabled(): List<AccountEntity>
    fun getAllEnabledFlow(): Flow<List<AccountEntity>>
    suspend fun getDefault(): AccountEntity?
    suspend fun setDefault(id: Long): Int
    suspend fun decreaseBalance(accountId: Long, amount: BigDecimal): Int
    suspend fun increaseBalance(accountId: Long, amount: BigDecimal): Int
    suspend fun adjustBalance(accountId: Long, oldAmount: BigDecimal, newAmount: BigDecimal): Int
}
