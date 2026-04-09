package com.example.wuganjizhang.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wuganjizhang.model.Category

@Dao
interface CategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun getAllCategoriesLive(): LiveData<List<Category>>

    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE type = :type AND enabled = 1 ORDER BY `order` ASC")
    fun getCategoriesByType(type: String): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE is_preset = 1 ORDER BY `order` ASC")
    suspend fun getPresetCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE is_preset = 0 ORDER BY `order` ASC")
    suspend fun getCustomCategories(): List<Category>

    @Query("UPDATE categories SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): Category?
}
