package com.seamless.bookkeeper.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class StatsUiState(
    val selectedDimension: String = "MONTH",
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val categoryStats: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = false
)

data class CategoryStat(
    val categoryName: String,
    val color: Int,
    val amount: BigDecimal,
    val percentage: Float
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    init {
        loadStats()
    }

    fun setDimension(dimension: String) {
        _uiState.value = _uiState.value.copy(selectedDimension = dimension)
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            // Stats loading logic
        }
    }
}
