package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.seamless.bookkeeper.data.local.entity.PendingTransactionEntity

@Dao
interface PendingTransactionDao {
    @Insert
    suspend fun insert(pending: PendingTransactionEntity): Long

    @Delete
    suspend fun delete(pending: PendingTransactionEntity): Int

    @Query("SELECT * FROM pending_transactions ORDER BY created_at DESC")
    suspend fun getAll(): List<PendingTransactionEntity>

    @Query("SELECT * FROM pending_transactions WHERE id = :id")
    suspend fun getById(id: Long): PendingTransactionEntity?

    @Query("DELETE FROM pending_transactions WHERE expires_at < :now")
    suspend fun deleteExpired(now: Long): Int

    @Query("SELECT COUNT(*) FROM pending_transactions")
    suspend fun count(): Int
}
