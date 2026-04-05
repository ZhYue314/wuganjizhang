package com.example.wuganjizhang.data.repository

import com.example.wuganjizhang.data.dao.AccountDao
import com.example.wuganjizhang.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {
    
    fun getAllEnabledAccounts(): Flow<List<Account>> {
        return accountDao.getAllEnabledAccounts()
    }
    
    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
    }
    
    suspend fun getAccountById(id: Long): Account? {
        return accountDao.getAccountById(id)
    }
    
    suspend fun getDefaultAccount(): Account? {
        return accountDao.getDefaultAccount()
    }
    
    suspend fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account)
    }
    
    suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }
    
    suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account)
    }
    
    suspend fun updateAccountBalance(id: Long, balance: Double) {
        accountDao.updateAccountBalance(id, balance)
    }
    
    suspend fun setDefaultAccount(id: Long) {
        accountDao.clearDefaultAccount()
        accountDao.setDefaultAccount(id)
    }
}
