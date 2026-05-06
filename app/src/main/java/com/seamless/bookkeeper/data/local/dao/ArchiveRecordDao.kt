package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.seamless.bookkeeper.data.local.entity.ArchiveRecordEntity

@Dao
interface ArchiveRecordDao {
    @Insert
    suspend fun insert(record: ArchiveRecordEntity): Long

    @Query("SELECT * FROM archive_records ORDER BY archive_time DESC")
    suspend fun getAll(): List<ArchiveRecordEntity>

    @Query("SELECT * FROM archive_records WHERE id = :id")
    suspend fun getById(id: Long): ArchiveRecordEntity?

    @Query("UPDATE archive_records SET is_restored = :restored WHERE id = :id")
    suspend fun updateRestored(id: Long, restored: Boolean): Int
}
