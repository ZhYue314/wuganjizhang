package com.seamless.bookkeeper.data.repository

import com.seamless.bookkeeper.data.local.dao.TransactionDao
import com.seamless.bookkeeper.data.local.entity.MerchantCategoryStat
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun insert(transaction: TransactionEntity): Long = transactionDao.insert(transaction)

    override suspend fun update(transaction: TransactionEntity): Int = transactionDao.update(transaction)

    override suspend fun delete(id: Long): Int = transactionDao.deleteById(id)

    override suspend fun getById(id: Long): TransactionEntity? = transactionDao.getById(id)

    override suspend fun getAll(limit: Int, offset: Int): List<TransactionEntity> = transactionDao.getAll(limit, offset)

    override fun getAllFlow(): Flow<List<TransactionEntity>> = transactionDao.getAllFlow()

    override suspend fun getByDateRange(start: Long, end: Long): List<TransactionEntity> = transactionDao.getByDateRange(start, end)

    override suspend fun existsDuplicate(amount: BigDecimal, merchantName: String?, timestampAfter: Long): Boolean {
        return transactionDao.existsDuplicate(amount, merchantName, timestampAfter) != null
    }

    override suspend fun findOriginal(amount: BigDecimal, merchantName: String?, beforeTimestamp: Long, withinMs: Long): TransactionEntity? {
        return transactionDao.findOriginalTransaction(amount, merchantName, beforeTimestamp, withinMs)
    }

    override suspend fun markAsRefunded(originalId: Long, refundId: Long): Int = transactionDao.markAsRefunded(originalId, refundId)

    override suspend fun updateNote(id: Long, note: String): Int = transactionDao.updateNote(id, note)

    override suspend fun deleteByIds(ids: List<Long>): Int = transactionDao.deleteByIds(ids)

    override suspend fun batchUpdateCategory(ids: List<Long>, categoryId: Long): Int = transactionDao.batchUpdateCategory(ids, categoryId)

    override suspend fun getTotalExpense(start: Long, end: Long): BigDecimal? = transactionDao.getTotalExpense(start, end)

    override suspend fun getTotalIncome(start: Long, end: Long): BigDecimal? = transactionDao.getTotalIncome(start, end)

    override suspend fun getMerchantCategoryStats(): List<MerchantCategoryStat> = transactionDao.getMerchantCategoryStats()

    override suspend fun findSimilar(merchantName: String?, excludeId: Long): List<TransactionEntity> = transactionDao.findSimilar(merchantName, excludeId)

    override suspend fun getAllWithRelations(limit: Int, offset: Int): List<TransactionWithRelations> = transactionDao.getAllWithRelations(limit, offset)

    override fun getAllWithRelationsFlow(): Flow<List<TransactionWithRelations>> = transactionDao.getAllWithRelationsFlow()
}
