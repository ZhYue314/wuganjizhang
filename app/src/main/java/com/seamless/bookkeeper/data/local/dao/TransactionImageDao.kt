package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.seamless.bookkeeper.data.local.entity.TransactionImageEntity

@Dao
interface TransactionImageDao {
    @Insert
    suspend fun insert(image: TransactionImageEntity): Long

    @Delete
    suspend fun delete(image: TransactionImageEntity): Int

    @Query("SELECT * FROM transaction_images WHERE transaction_id = :txId ORDER BY created_at ASC")
    suspend fun getByTransactionId(txId: Long): List<TransactionImageEntity>

    @Query("DELETE FROM transaction_images WHERE transaction_id = :txId")
    suspend fun deleteByTransactionId(txId: Long): Int
}
