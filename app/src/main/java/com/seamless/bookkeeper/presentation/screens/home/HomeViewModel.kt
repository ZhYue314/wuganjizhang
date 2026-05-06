package com.seamless.bookkeeper.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import com.seamless.bookkeeper.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val transactions: List<TransactionWithRelations> = emptyList(),
    val groupedTransactions: Map<String, List<TransactionWithRelations>> = emptyMap(),
    val dailyTotals: Map<String, BigDecimal> = emptyMap(),
    val monthlyExpense: BigDecimal = BigDecimal.ZERO,
    val monthlyIncome: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val isLoading: Boolean = true,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val transactions = transactionRepository.getAllWithRelations(limit = 200)
            val now = System.currentTimeMillis()
            val monthStart = DateUtil.getMonthStart(now)
            val monthEnd = DateUtil.getMonthEnd(now)

            val expense = transactionRepository.getTotalExpense(monthStart, monthEnd) ?: BigDecimal.ZERO
            val income = transactionRepository.getTotalIncome(monthStart, monthEnd) ?: BigDecimal.ZERO

            val grouped = groupByDate(transactions)

            _uiState.value = HomeUiState(
                transactions = transactions,
                groupedTransactions = grouped,
                dailyTotals = grouped.mapValues { (_, txs) ->
                    txs.map { it.transaction }.filter { it.type == "EXPENSE" }
                        .sumOf { it.amount } ?: BigDecimal.ZERO
                },
                monthlyExpense = expense,
                monthlyIncome = income,
                balance = income.subtract(expense),
                isLoading = false
            )
        }
    }

    fun toggleSelectionMode() {
        val current = _uiState.value
        if (current.isSelectionMode) {
            _uiState.value = current.copy(isSelectionMode = false, selectedIds = emptySet())
        } else {
            _uiState.value = current.copy(isSelectionMode = true)
        }
    }

    fun toggleSelection(id: Long) {
        val current = _uiState.value
        val newSelected = if (id in current.selectedIds) {
            current.selectedIds - id
        } else {
            current.selectedIds + id
        }
        _uiState.value = current.copy(selectedIds = newSelected)
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val ids = _uiState.value.selectedIds.toList()
            transactionRepository.deleteByIds(ids)
            _uiState.value = _uiState.value.copy(isSelectionMode = false, selectedIds = emptySet())
            loadTransactions()
        }
    }

    fun deleteTransaction(tx: TransactionWithRelations) {
        viewModelScope.launch {
            transactionRepository.delete(tx.transaction.id)
            loadTransactions()
        }
    }

    private fun groupByDate(transactions: List<TransactionWithRelations>): Map<String, List<TransactionWithRelations>> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        return transactions.groupBy { tx ->
            val date = Instant.ofEpochMilli(tx.transaction.timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            when (date) {
                today -> "今天"
                yesterday -> "昨天"
                else -> "${date.monthValue}月${date.dayOfMonth}日"
            }
        }
    }

    fun formatAmount(amount: BigDecimal): String {
        return "¥${amount.setScale(2, BigDecimal.ROUND_HALF_UP)}"
    }
}
