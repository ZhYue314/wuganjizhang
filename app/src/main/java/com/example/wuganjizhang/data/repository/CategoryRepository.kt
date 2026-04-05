package com.example.wuganjizhang.data.repository

import com.example.wuganjizhang.data.dao.CategoryDao
import com.example.wuganjizhang.data.model.Category
import com.example.wuganjizhang.data.model.CategoryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }
    
    fun getAllEnabledCategories(): Flow<List<Category>> {
        return categoryDao.getAllEnabledCategories()
    }
    
    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }
    
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
    
    suspend fun toggleCategoryEnabled(id: Long, enabled: Boolean) {
        categoryDao.toggleCategoryEnabled(id, enabled)
    }
    
    suspend fun getCustomCategoryCount(): Int {
        return categoryDao.getCustomCategoryCount()
    }
}
