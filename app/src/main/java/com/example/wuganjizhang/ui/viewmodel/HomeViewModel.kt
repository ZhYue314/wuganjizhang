package com.example.wuganjizhang.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuganjizhang.data.local.dao.TransactionDao
import com.example.wuganjizhang.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val transactions: List<Transaction> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMonthlyData()
        observeTransactions()
    }

    /**
     * 加载月度统计数据
     */
    private fun loadMonthlyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val calendar = Calendar.getInstance()
                val startOfMonth = getStartOfMonth(calendar)
                val endOfMonth = System.currentTimeMillis()

                // 观察月度收入
                transactionDao.getTotalIncomeByDateRange(startOfMonth, endOfMonth)
                    .observeForever { income ->
                        val totalIncome = income ?: 0.0
                        updateMonthlyStats(totalIncome, _uiState.value.monthlyExpense)
                    }

                // 观察月度支出
                transactionDao.getTotalExpenseByDateRange(startOfMonth, endOfMonth)
                    .observeForever { expense ->
                        val totalExpense = expense ?: 0.0
                        updateMonthlyStats(_uiState.value.monthlyIncome, totalExpense)
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * 观察交易列表变化
     */
    private fun observeTransactions() {
        viewModelScope.launch {
            transactionDao.getAllTransactionsLive().observeForever { transactions ->
                _uiState.value = _uiState.value.copy(
                    transactions = transactions ?: emptyList(),
                    isLoading = false
                )
                // 当交易列表变化时，重新加载月度统计
                loadMonthlyData()
            }
        }
    }

    /**
     * 更新月度统计
     */
    private fun updateMonthlyStats(income: Double, expense: Double) {
        _uiState.value = _uiState.value.copy(
            monthlyIncome = income,
            monthlyExpense = expense,
            monthlyBalance = income - expense,
            isLoading = false
        )
    }

    /**
     * 删除交易
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.softDelete(transaction.id)
        }
    }

    /**
     * 获取月份起始时间戳
     */
    private fun getStartOfMonth(calendar: Calendar): Long {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 格式化金额
     */
    fun formatAmount(amount: Double): String {
        return String.format("¥%.2f", amount)
    }

    /**
     * 格式化时间
     */
    fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 按日期分组交易
     */
    fun groupTransactionsByDate(transactions: List<Transaction>): Map<String, List<Transaction>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val today = dateFormat.format(Date())
        val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))

        return transactions.groupBy { transaction ->
            val dateStr = dateFormat.format(Date(transaction.timestamp))
            when (dateStr) {
                today -> "今天"
                yesterday -> "昨天"
                else -> dateStr
            }
        }
    }
}
