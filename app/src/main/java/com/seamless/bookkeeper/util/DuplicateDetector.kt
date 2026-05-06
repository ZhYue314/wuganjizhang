package com.seamless.bookkeeper.util

import com.seamless.bookkeeper.data.local.dao.TransactionDao
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DuplicateDetector @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun findDuplicates(): List<List<TransactionEntity>> {
        return emptyList()
    }

    suspend fun mergeDuplicates(keepId: Long, mergeIds: List<Long>) {
        mergeIds.forEach { id ->
            transactionDao.deleteById(id)
        }
    }
}
