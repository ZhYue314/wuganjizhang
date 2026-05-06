package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Delete
    suspend fun delete(category: CategoryEntity): Int

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM categories WHERE is_enabled = 1 AND type = :type ORDER BY sort_order ASC")
    suspend fun getByType(type: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE is_enabled = 1 AND type = :type ORDER BY sort_order ASC")
    fun getByTypeFlow(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE is_preset = 1 AND is_enabled = 1 ORDER BY sort_order ASC")
    suspend fun getPresetCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE is_preset = 0 ORDER BY sort_order ASC")
    suspend fun getCustomCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE type = 'EXPENSE' AND is_enabled = 1 ORDER BY sort_order ASC LIMIT 1")
    suspend fun getDefaultExpenseCategory(): CategoryEntity?

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    fun getAllFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}
