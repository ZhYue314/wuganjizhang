package com.seamless.bookkeeper.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import com.seamless.bookkeeper.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class StatsUiState(
    val selectedDimension: String = "MONTH",
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val transactionCount: Int = 0,
    val dailyAvg: BigDecimal = BigDecimal.ZERO,
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val isLoading: Boolean = false
)

data class CategoryBreakdown(
    val categoryName: String,
    val color: Int,
    val amount: BigDecimal,
    val percentage: Float
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    init { loadStats() }

    fun setDimension(dimension: String) {
        _uiState.value = _uiState.value.copy(selectedDimension = dimension)
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val now = System.currentTimeMillis()
            val (start, end) = getTimeRange(now)
            val expense = transactionRepository.getTotalExpense(start, end) ?: BigDecimal.ZERO
            val income = transactionRepository.getTotalIncome(start, end) ?: BigDecimal.ZERO
            val transactions = transactionRepository.getByDateRange(start, end)
            val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(end - start).coerceAtLeast(1)
            val dailyAvg = if (days > 0) expense.divide(BigDecimal(days), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO

            // Calculate category breakdown
            val categories = categoryRepository.getByType("EXPENSE")
            val categoryTotals = transactions
                .filter { it.type == "EXPENSE" && it.categoryId != null }
                .groupBy { it.categoryId }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } ?: BigDecimal.ZERO }

            val totalExpenseAmount = categoryTotals.values.sumOf { it } ?: BigDecimal.ONE
            val breakdown = categoryTotals.entries.mapNotNull { (catId, amount) ->
                val cat = categories.find { it.id == catId } ?: return@mapNotNull null
                CategoryBreakdown(
                    categoryName = cat.name,
                    color = cat.color,
                    amount = amount,
                    percentage = amount.divide(totalExpenseAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100)).setScale(1, RoundingMode.HALF_UP).toFloat()
                )
            }.sortedByDescending { it.amount }

            _uiState.value = StatsUiState(
                selectedDimension = _uiState.value.selectedDimension,
                totalExpense = expense,
                totalIncome = income,
                balance = income.subtract(expense),
                transactionCount = transactions.size,
                dailyAvg = dailyAvg,
                categoryBreakdown = if (breakdown.isEmpty()) {
                    listOf(CategoryBreakdown("未分类", 0xFF97A597.toInt(), expense, 100f))
                } else breakdown,
                isLoading = false
            )
        }
    }

    private fun getTimeRange(now: Long): Pair<Long, Long> {
        return when (_uiState.value.selectedDimension) {
            "DAY" -> {
                val start = DateUtil.getTodayStart()
                start to (start + 86400000L - 1)
            }
            "WEEK" -> {
                val cal = java.util.Calendar.getInstance()
                cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                cal.set(java.util.Calendar.MINUTE, 0)
                cal.set(java.util.Calendar.SECOND, 0)
                cal.set(java.util.Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                start to (start + 7 * 86400000L - 1)
            }
            "MONTH" -> DateUtil.getMonthStart(now) to DateUtil.getMonthEnd(now)
            "YEAR" -> {
                val cal = java.util.Calendar.getInstance()
                cal.set(java.util.Calendar.DAY_OF_YEAR, 1)
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                cal.set(java.util.Calendar.MINUTE, 0)
                cal.set(java.util.Calendar.SECOND, 0)
                cal.set(java.util.Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(java.util.Calendar.DAY_OF_YEAR, cal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR))
                cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                start to cal.timeInMillis
            }
            else -> DateUtil.getMonthStart(now) to DateUtil.getMonthEnd(now)
        }
    }
}
