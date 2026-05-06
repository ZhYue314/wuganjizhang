package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.PeriodicTemplateEntity

@Dao
interface PeriodicTemplateDao {
    @Insert
    suspend fun insert(template: PeriodicTemplateEntity): Long

    @Update
    suspend fun update(template: PeriodicTemplateEntity): Int

    @Delete
    suspend fun delete(template: PeriodicTemplateEntity): Int

    @Query("SELECT * FROM periodic_templates WHERE is_enabled = 1")
    suspend fun getAllEnabled(): List<PeriodicTemplateEntity>

    @Query("SELECT * FROM periodic_templates WHERE id = :id")
    suspend fun getById(id: Long): PeriodicTemplateEntity?

    @Query("SELECT * FROM periodic_templates")
    suspend fun getAll(): List<PeriodicTemplateEntity>

    @Query("UPDATE periodic_templates SET last_generated_at = :timestamp WHERE id = :id")
    suspend fun updateLastGenerated(id: Long, timestamp: Long): Int
}
