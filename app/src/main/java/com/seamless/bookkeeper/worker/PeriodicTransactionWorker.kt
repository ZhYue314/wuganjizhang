package com.seamless.bookkeeper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seamless.bookkeeper.data.local.dao.AccountDao
import com.seamless.bookkeeper.data.local.dao.PeriodicTemplateDao
import com.seamless.bookkeeper.data.local.dao.TransactionDao
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.math.BigDecimal
import java.util.Calendar

@HiltWorker
class PeriodicTransactionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val periodicTemplateDao: PeriodicTemplateDao,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val templates = periodicTemplateDao.getAllEnabled()
            val now = System.currentTimeMillis()

            templates.forEach { template ->
                val nextDue = calculateNextDue(template.lastGeneratedAt ?: template.startDate, template.period)
                if (nextDue <= now) {
                    val tx = TransactionEntity(
                        amount = template.amount,
                        type = template.type,
                        categoryId = template.categoryId,
                        accountId = template.accountId,
                        merchantName = template.merchantName,
                        timestamp = nextDue,
                        source = "PERIODIC",
                        createdAt = now,
                        updatedAt = now
                    )
                    transactionDao.insert(tx)
                    periodicTemplateDao.updateLastGenerated(template.id, nextDue)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun calculateNextDue(lastGen: Long, period: String): Long {
        return Calendar.getInstance().apply {
            timeInMillis = lastGen
            when (period) {
                "DAILY" -> add(Calendar.DAY_OF_MONTH, 1)
                "WEEKLY" -> add(Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> add(Calendar.MONTH, 1)
                "YEARLY" -> add(Calendar.YEAR, 1)
            }
        }.timeInMillis
    }
}
