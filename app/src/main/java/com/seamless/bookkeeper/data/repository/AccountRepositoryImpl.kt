package com.seamless.bookkeeper.data.repository

import com.seamless.bookkeeper.data.local.dao.AccountDao
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun insert(account: AccountEntity): Long = accountDao.insert(account)

    override suspend fun update(account: AccountEntity): Int = accountDao.update(account)

    override suspend fun delete(id: Long): Int = accountDao.deleteById(id)

    override suspend fun getById(id: Long): AccountEntity? = accountDao.getById(id)

    override suspend fun getAllEnabled(): List<AccountEntity> = accountDao.getAllEnabled()

    override fun getAllEnabledFlow(): Flow<List<AccountEntity>> = accountDao.getAllEnabledFlow()

    override suspend fun getDefault(): AccountEntity? = accountDao.getDefault()

    override suspend fun setDefault(id: Long): Int = accountDao.setDefault(id)

    override suspend fun decreaseBalance(accountId: Long, amount: BigDecimal): Int = accountDao.decreaseBalance(accountId, amount)

    override suspend fun increaseBalance(accountId: Long, amount: BigDecimal): Int = accountDao.increaseBalance(accountId, amount)

    override suspend fun adjustBalance(accountId: Long, oldAmount: BigDecimal, newAmount: BigDecimal): Int = accountDao.adjustBalance(accountId, oldAmount, newAmount)
}
