package com.example.wuganjizhang.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuganjizhang.data.local.AppDatabase
import com.example.wuganjizhang.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class StatsUiState(
    val timeDimension: TimeDimension = TimeDimension.MONTH,
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val dailyStats: List<DailyStat> = emptyList(),
    val isLoading: Boolean = false
)

enum class TimeDimension {
    DAY, WEEK, MONTH, YEAR
}

data class CategoryStat(
    val categoryId: Int,
    val categoryName: String,
    val amount: Double,
    val percentage: Double,
    val color: Int  // ARGB 颜色值
)

data class DailyStat(
    val date: String,
    val income: Double,
    val expense: Double
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun setTimeDimension(dimension: TimeDimension) {
        _uiState.value = _uiState.value.copy(timeDimension = dimension)
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val transactions = database.transactionDao().getAllTransactions()
            val filteredTransactions = filterByTimeDimension(transactions, _uiState.value.timeDimension)
            
            val income = filteredTransactions.filter { it.type == "income" }.sumOf { it.amount }
            val expense = filteredTransactions.filter { it.type == "expense" }.sumOf { it.amount }
            
            val categoryStats = calculateCategoryStats(filteredTransactions)
            val dailyStats = calculateDailyStats(filteredTransactions)
            
            _uiState.value = _uiState.value.copy(
                transactions = filteredTransactions,
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense,
                categoryStats = categoryStats,
                dailyStats = dailyStats,
                isLoading = false
            )
        }
    }

    private fun filterByTimeDimension(
        transactions: List<Transaction>,
        dimension: TimeDimension
    ): List<Transaction> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        return when (dimension) {
            TimeDimension.DAY -> {
                val startOfDay = getStartOfDay(now)
                transactions.filter { it.timestamp >= startOfDay }
            }
            TimeDimension.WEEK -> {
                val startOfWeek = getStartOfWeek(now)
                transactions.filter { it.timestamp >= startOfWeek }
            }
            TimeDimension.MONTH -> {
                val startOfMonth = getStartOfMonth(now)
                transactions.filter { it.timestamp >= startOfMonth }
            }
            TimeDimension.YEAR -> {
                val startOfYear = getStartOfYear(now)
                transactions.filter { it.timestamp >= startOfYear }
            }
        }
    }

    private fun calculateCategoryStats(transactions: List<Transaction>): List<CategoryStat> {
        val expenseTransactions = transactions.filter { it.type == "expense" }
        val totalExpense = expenseTransactions.sumOf { it.amount }
        
        if (totalExpense == 0.0) return emptyList()
        
        val categoryGroups = expenseTransactions.groupBy { it.categoryId }
        
        return categoryGroups.map { (categoryId, txs) ->
            val amount = txs.sumOf { it.amount }
            val percentage = (amount / totalExpense) * 100
            val categoryName = txs.firstOrNull()?.categoryName ?: "未分类"
            
            // 生成颜色（基于 categoryId）
            val color = generateColor( categoryId)
            
            CategoryStat(
                categoryId = categoryId,
                categoryName = categoryName,
                amount = amount,
                percentage = percentage,
                color = color
            )
        }.sortedByDescending { it.amount }
    }

    private fun calculateDailyStats(transactions: List<Transaction>): List<DailyStat> {
        val dateFormat = SimpleDateFormat("MM-dd", Locale.CHINA)
        val grouped = transactions.groupBy { 
            dateFormat.format(Date(it.timestamp)) 
        }
        
        return grouped.map { (date, txs) ->
            val income = txs.filter { it.type == "income" }.sumOf { it.amount }
            val expense = txs.filter { it.type == "expense" }.sumOf { it.amount }
            DailyStat(date = date, income = income, expense = expense)
        }.sortedBy { it.date }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfWeek(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfYear(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun generateColor(id: Int): Int {
        // 生成一致的颜色
        val colors = listOf(
            0xFF4F6EF7.toInt(), // 主色
            0xFF22C55E.toInt(), // 收入色
            0xFFEF4444.toInt(), // 支出色
            0xFFF59E0B.toInt(), // 警告色
            0xFF3B82F6.toInt(), // 信息色
            0xFF8B5CF6.toInt(), // 紫色
            0xFFEC4899.toInt(), // 粉色
            0xFF14B8A6.toInt()  // 青色
        )
        return colors[id % colors.size]
    }
}
