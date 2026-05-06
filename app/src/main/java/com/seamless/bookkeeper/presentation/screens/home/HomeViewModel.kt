package com.seamless.bookkeeper.presentation.screens.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch {
            transactionRepository.getAllWithRelationsFlow().collect { transactions ->
                val grouped = groupByDate(transactions)
                val now = System.currentTimeMillis()
                val ms = DateUtil.getMonthStart(now)
                val me = DateUtil.getMonthEnd(now)
                val expense = transactionRepository.getTotalExpense(ms, me) ?: BigDecimal.ZERO
                val income = transactionRepository.getTotalIncome(ms, me) ?: BigDecimal.ZERO
                _uiState.value = _uiState.value.copy(
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
    }

    fun toggleSelectionMode() {
        val cur = _uiState.value
        _uiState.value = cur.copy(
            isSelectionMode = !cur.isSelectionMode,
            selectedIds = if (cur.isSelectionMode) emptySet() else cur.selectedIds
        )
    }

    fun toggleSelection(id: Long) {
        val cur = _uiState.value
        _uiState.value = cur.copy(
            selectedIds = if (id in cur.selectedIds) cur.selectedIds - id else cur.selectedIds + id
        )
    }

    fun deleteSelected() {
        viewModelScope.launch {
            transactionRepository.deleteByIds(_uiState.value.selectedIds.toList())
            _uiState.value = _uiState.value.copy(isSelectionMode = false, selectedIds = emptySet())
        }
    }

    fun deleteTransaction(tx: TransactionWithRelations) {
        viewModelScope.launch { transactionRepository.delete(tx.transaction.id) }
    }

    private fun groupByDate(transactions: List<TransactionWithRelations>): Map<String, List<TransactionWithRelations>> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val map = LinkedHashMap<String, List<TransactionWithRelations>>()
        for (tx in transactions) {
            val date = Instant.ofEpochMilli(tx.transaction.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            val key = when (date) { today -> "今天"; yesterday -> "昨天"; else -> "${date.monthValue}月${date.dayOfMonth}日" }
            map[key] = (map[key] ?: emptyList()) + tx
        }
        return map
    }

    fun formatAmount(amount: BigDecimal): String = "¥${amount.setScale(2, BigDecimal.ROUND_HALF_UP)}"
}
