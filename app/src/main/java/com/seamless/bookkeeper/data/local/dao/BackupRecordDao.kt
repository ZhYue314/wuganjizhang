package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.seamless.bookkeeper.data.local.entity.BackupRecordEntity

@Dao
interface BackupRecordDao {
    @Insert
    suspend fun insert(record: BackupRecordEntity): Long

    @Query("SELECT * FROM backup_records ORDER BY backup_time DESC LIMIT 1")
    suspend fun getLatestSuccess(): BackupRecordEntity?

    @Query("SELECT * FROM backup_records ORDER BY backup_time DESC")
    suspend fun getAll(): List<BackupRecordEntity>

    @Delete
    suspend fun delete(record: BackupRecordEntity): Int
}
