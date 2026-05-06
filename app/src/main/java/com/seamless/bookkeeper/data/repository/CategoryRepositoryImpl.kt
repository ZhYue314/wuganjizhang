package com.seamless.bookkeeper.data.repository

import com.seamless.bookkeeper.data.local.dao.CategoryDao
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun insert(category: CategoryEntity): Long = categoryDao.insert(category)

    override suspend fun update(category: CategoryEntity): Int = categoryDao.update(category)

    override suspend fun delete(id: Long): Int = categoryDao.deleteById(id)

    override suspend fun getById(id: Long): CategoryEntity? = categoryDao.getById(id)

    override suspend fun getByType(type: String): List<CategoryEntity> = categoryDao.getByType(type)

    override fun getByTypeFlow(type: String): Flow<List<CategoryEntity>> = categoryDao.getByTypeFlow(type)

    override suspend fun getPresetCategories(): List<CategoryEntity> = categoryDao.getPresetCategories()

    override suspend fun getCustomCategories(): List<CategoryEntity> = categoryDao.getCustomCategories()

    override suspend fun getDefaultExpenseCategory(): CategoryEntity? = categoryDao.getDefaultExpenseCategory()

    override suspend fun getAll(): List<CategoryEntity> = categoryDao.getAll()

    override fun getAllFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllFlow()
}
