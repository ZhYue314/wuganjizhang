package com.example.wuganjizhang.data.dao

import androidx.room.*
import com.example.wuganjizhang.data.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts WHERE isEnabled = 1 ORDER BY id ASC")
    fun getAllEnabledAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Long): Account?
    
    @Query("SELECT * FROM accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAccount(): Account?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long
    
    @Update
    suspend fun updateAccount(account: Account)
    
    @Delete
    suspend fun deleteAccount(account: Account)
    
    @Query("UPDATE accounts SET currentBalance = :balance WHERE id = :id")
    suspend fun updateAccountBalance(id: Long, balance: Double)
    
    @Query("UPDATE accounts SET isDefault = 0")
    suspend fun clearDefaultAccount()
    
    @Query("UPDATE accounts SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAccount(id: Long)
}
