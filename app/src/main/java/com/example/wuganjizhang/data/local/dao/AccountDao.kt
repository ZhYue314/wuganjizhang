package com.example.wuganjizhang.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wuganjizhang.model.Account

@Dao
interface AccountDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<Account>)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM accounts ORDER BY `order` ASC")
    fun getAllAccountsLive(): LiveData<List<Account>>

    @Query("SELECT * FROM accounts ORDER BY `order` ASC")
    suspend fun getAllAccounts(): List<Account>

    @Query("SELECT * FROM accounts WHERE enabled = 1 ORDER BY `order` ASC")
    fun getEnabledAccountsLive(): LiveData<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Int): Account?

    @Query("SELECT * FROM accounts WHERE type = :type LIMIT 1")
    suspend fun getAccountByType(type: String): Account?

    @Query("UPDATE accounts SET balance = :balance WHERE id = :id")
    suspend fun updateBalance(id: Int, balance: Double)

    @Query("SELECT SUM(balance) FROM accounts WHERE enabled = 1")
    suspend fun getTotalBalance(): Double

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int

    @Query("UPDATE accounts SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean)
}
