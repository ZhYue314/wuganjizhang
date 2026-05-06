package com.seamless.bookkeeper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.seamless.bookkeeper.data.local.entity.MerchantCategoryStat
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.entity.TransactionTagCrossRef
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun update(transaction: TransactionEntity): Int

    @Delete
    suspend fun delete(transaction: TransactionEntity): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>): Int

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
        ORDER BY timestamp DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getAll(limit: Int = 50, offset: Int = 0): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE is_archived = 0 ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
          AND timestamp BETWEEN :start AND :end
        ORDER BY timestamp DESC
    """)
    suspend fun getByDateRange(start: Long, end: Long): List<TransactionEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
          AND timestamp >= :dayStart
          AND timestamp < :nextDayStart
        ORDER BY timestamp DESC
    """)
    suspend fun getByDate(dayStart: Long, nextDayStart: Long): List<TransactionEntity>

    @Query("UPDATE transactions SET is_archived = 1 WHERE id IN (:ids)")
    suspend fun markAsArchived(ids: List<Long>): Int

    @Query("UPDATE transactions SET category_id = :categoryId WHERE id IN (:ids)")
    suspend fun batchUpdateCategory(ids: List<Long>, categoryId: Long): Int

    @Query("UPDATE transactions SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String): Int

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
          AND amount = :amount
          AND (:merchantName IS NULL OR merchant_name = :merchantName)
          AND timestamp > :timestampAfter
        LIMIT 1
    """)
    suspend fun existsDuplicate(amount: BigDecimal, merchantName: String?, timestampAfter: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
          AND amount = :amount
          AND (:merchantName IS NULL OR merchant_name LIKE '%' || :merchantName || '%')
          AND timestamp < :beforeTimestamp
          AND timestamp > (:beforeTimestamp - :withinMs)
          AND type = 'EXPENSE'
        ORDER BY timestamp DESC
        LIMIT 1
    """)
    suspend fun findOriginalTransaction(amount: BigDecimal, merchantName: String?, beforeTimestamp: Long, withinMs: Long): TransactionEntity?

    @Query("UPDATE transactions SET is_refund = 1, original_transaction_id = :refundId WHERE id = :originalId")
    suspend fun markAsRefunded(originalId: Long, refundId: Long): Int

    @Query("""
        SELECT id FROM transactions
        WHERE is_archived = 0
          AND (:beforeDate IS NULL OR timestamp < :beforeDate)
          AND (:categoryIds IS NULL OR category_id IN (:categoryIds))
          AND (:maxAmount IS NULL OR amount <= :maxAmount)
    """)
    suspend fun findIdsByCriteria(beforeDate: Long?, categoryIds: List<Long>?, maxAmount: BigDecimal?): List<Long>

    @Insert
    suspend fun batchInsertTags(crossRefs: List<TransactionTagCrossRef>): List<Long>

    @Query("""
        SELECT t.*, c.name as category_name, c.color as category_color,
               a.name as account_name
        FROM transactions t
        LEFT JOIN categories c ON t.category_id = c.id
        LEFT JOIN accounts a ON t.account_id = a.id
        WHERE t.is_archived = 0
        ORDER BY t.timestamp DESC
        LIMIT :limit OFFSET :offset
    """)
    @Transaction
    suspend fun getAllWithRelations(limit: Int = 50, offset: Int = 0): List<TransactionWithRelations>

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE is_archived = 0 AND type = 'EXPENSE'
          AND timestamp BETWEEN :start AND :end
    """)
    suspend fun getTotalExpense(start: Long, end: Long): BigDecimal?

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE is_archived = 0 AND type = 'INCOME'
          AND timestamp BETWEEN :start AND :end
    """)
    suspend fun getTotalIncome(start: Long, end: Long): BigDecimal?

    @Query("""
        SELECT merchant_name, category_id, COUNT(*) as cnt
        FROM transactions
        WHERE is_archived = 0
          AND merchant_name IS NOT NULL
          AND category_id IS NOT NULL
        GROUP BY merchant_name, category_id
        HAVING cnt >= 3
        ORDER BY cnt DESC
    """)
    suspend fun getMerchantCategoryStats(): List<MerchantCategoryStat>

    @Query("""
        SELECT * FROM transactions
        WHERE is_archived = 0
          AND (:merchantName IS NULL OR merchant_name = :merchantName)
          AND id != :excludeId
        ORDER BY timestamp DESC
        LIMIT 20
    """)
    suspend fun findSimilar(merchantName: String?, excludeId: Long): List<TransactionEntity>

    @RawQuery(observedEntities = [TransactionEntity::class])
    suspend fun searchRaw(query: SupportSQLiteQuery): List<TransactionEntity>
}
