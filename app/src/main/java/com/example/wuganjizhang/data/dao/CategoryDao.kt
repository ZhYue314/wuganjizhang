package com.example.wuganjizhang.data.dao

import androidx.room.*
import com.example.wuganjizhang.data.model.Category
import com.example.wuganjizhang.data.model.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC")
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE isEnabled = 1 ORDER BY sortOrder ASC")
    fun getAllEnabledCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("UPDATE categories SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleCategoryEnabled(id: Long, enabled: Boolean)
    
    @Query("SELECT COUNT(*) FROM categories WHERE isPreset = 0")
    suspend fun getCustomCategoryCount(): Int
}
