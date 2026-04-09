package com.example.wuganjizhang.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wuganjizhang.model.Transaction

@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM transactions WHERE deleted = 0 ORDER BY timestamp DESC")
    fun getAllTransactionsLive(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND category_id = :categoryId ORDER BY timestamp DESC")
    fun getTransactionsByCategory(categoryId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND account_id = :accountId ORDER BY timestamp DESC")
    fun getTransactionsByAccount(accountId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startTime: Long, endTime: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND (merchant LIKE '%' || :keyword || '%' OR remark LIKE '%' || :keyword || '%') ORDER BY timestamp DESC")
    fun searchTransactions(keyword: String): LiveData<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE deleted = 0 AND type = 'expense' AND timestamp >= :startTime AND timestamp <= :endTime")
    fun getTotalExpenseByDateRange(startTime: Long, endTime: Long): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE deleted = 0 AND type = 'income' AND timestamp >= :startTime AND timestamp <= :endTime")
    fun getTotalIncomeByDateRange(startTime: Long, endTime: Long): LiveData<Double?>

    @Query("SELECT COUNT(*) FROM transactions WHERE deleted = 0")
    suspend fun getTransactionCount(): Int

    @Query("UPDATE transactions SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Int)

    @Query("DELETE FROM transactions WHERE deleted = 1")
    suspend fun purgeDeleted()

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND strftime('%Y-%m', timestamp/1000, 'unixepoch') = :yearMonth ORDER BY timestamp DESC")
    fun getTransactionsByMonth(yearMonth: String): LiveData<List<Transaction>>
}
