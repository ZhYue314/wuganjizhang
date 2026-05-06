package com.seamless.bookkeeper.domain.repository

import com.seamless.bookkeeper.data.local.entity.MerchantCategoryStat
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {
    suspend fun insert(transaction: TransactionEntity): Long
    suspend fun update(transaction: TransactionEntity): Int
    suspend fun delete(id: Long): Int
    suspend fun getById(id: Long): TransactionEntity?
    suspend fun getAll(limit: Int = 50, offset: Int = 0): List<TransactionEntity>
    fun getAllFlow(): Flow<List<TransactionEntity>>
    suspend fun getByDateRange(start: Long, end: Long): List<TransactionEntity>
    suspend fun existsDuplicate(amount: BigDecimal, merchantName: String?, timestampAfter: Long): Boolean
    suspend fun findOriginal(amount: BigDecimal, merchantName: String?, beforeTimestamp: Long, withinMs: Long): TransactionEntity?
    suspend fun markAsRefunded(originalId: Long, refundId: Long): Int
    suspend fun updateNote(id: Long, note: String): Int
    suspend fun deleteByIds(ids: List<Long>): Int
    suspend fun batchUpdateCategory(ids: List<Long>, categoryId: Long): Int
    suspend fun getTotalExpense(start: Long, end: Long): BigDecimal?
    suspend fun getTotalIncome(start: Long, end: Long): BigDecimal?
    suspend fun getMerchantCategoryStats(): List<MerchantCategoryStat>
    suspend fun findSimilar(merchantName: String?, excludeId: Long): List<TransactionEntity>
    suspend fun getAllWithRelations(limit: Int = 50, offset: Int = 0): List<TransactionWithRelations>
}
