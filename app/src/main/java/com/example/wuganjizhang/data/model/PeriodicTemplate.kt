package com.example.wuganjizhang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "periodic_templates")
data class PeriodicTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long?,
    val accountId: Long,
    val toAccountId: Long? = null,
    val merchant: String? = null,
    val period: PeriodType, // DAILY, WEEKLY, MONTHLY, YEARLY
    val startDate: Date,
    val endDate: Date? = null,
    val isEnabled: Boolean = true,
    val note: String? = null,
    val lastExecutedDate: Date? = null,
    val nextExecutionDate: Date? = null
)

enum class PeriodType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}
