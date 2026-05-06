package com.seamless.bookkeeper.domain.repository

import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insert(category: CategoryEntity): Long
    suspend fun update(category: CategoryEntity): Int
    suspend fun delete(id: Long): Int
    suspend fun getById(id: Long): CategoryEntity?
    suspend fun getByType(type: String): List<CategoryEntity>
    fun getByTypeFlow(type: String): Flow<List<CategoryEntity>>
    suspend fun getPresetCategories(): List<CategoryEntity>
    suspend fun getCustomCategories(): List<CategoryEntity>
    suspend fun getDefaultExpenseCategory(): CategoryEntity?
    suspend fun getAll(): List<CategoryEntity>
    fun getAllFlow(): Flow<List<CategoryEntity>>
}
