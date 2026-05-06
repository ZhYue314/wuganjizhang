package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: AccountEntity): Long

    @Insert
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Update
    suspend fun update(account: AccountEntity): Int

    @Delete
    suspend fun delete(account: AccountEntity): Int

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM accounts WHERE is_enabled = 1 ORDER BY sort_order ASC")
    suspend fun getAllEnabled(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE is_enabled = 1 ORDER BY sort_order ASC")
    fun getAllEnabledFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE is_default = 1 LIMIT 1")
    suspend fun getDefault(): AccountEntity?

    @Query("UPDATE accounts SET is_default = 0")
    suspend fun clearAllDefault()

    @Query("UPDATE accounts SET is_default = 1 WHERE id = :id")
    suspend fun setDefault(id: Long): Int

    @Query("UPDATE accounts SET current_balance = current_balance - :amount WHERE id = :accountId")
    suspend fun decreaseBalance(accountId: Long, amount: BigDecimal): Int

    @Query("UPDATE accounts SET current_balance = current_balance + :amount WHERE id = :accountId")
    suspend fun increaseBalance(accountId: Long, amount: BigDecimal): Int

    @Query("UPDATE accounts SET current_balance = current_balance - :oldAmount + :newAmount WHERE id = :accountId")
    suspend fun adjustBalance(accountId: Long, oldAmount: BigDecimal, newAmount: BigDecimal): Int

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getCount(): Int
}
