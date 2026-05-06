package com.seamless.bookkeeper.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.local.entity.TransactionWithRelations
import com.seamless.bookkeeper.domain.repository.AccountRepository
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import com.seamless.bookkeeper.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class SearchFilters(
    val keyword: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val minAmount: BigDecimal? = null,
    val maxAmount: BigDecimal? = null,
    val categoryIds: Set<Long> = emptySet(),
    val accountIds: Set<Long> = emptySet(),
    val types: Set<String> = emptySet()
) {
    val hasActiveFilters: Boolean get() = keyword.isNotBlank() || startDate != null || endDate != null ||
            minAmount != null || maxAmount != null || categoryIds.isNotEmpty() || accountIds.isNotEmpty() || types.isNotEmpty()
}

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val results: List<TransactionWithRelations> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val categories: List<com.seamless.bookkeeper.data.local.entity.CategoryEntity> = emptyList(),
    val accounts: List<com.seamless.bookkeeper.data.local.entity.AccountEntity> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init { loadFilterData() }

    private fun loadFilterData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                categories = categoryRepository.getAll(),
                accounts = accountRepository.getAllEnabled()
            )
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun search() {
        viewModelScope.launch {
            val state = _uiState.value
            val keyword = state.query.trim().ifBlank { null }
            val start = state.filters.startDate
            val end = state.filters.endDate

            if (keyword == null && !state.filters.hasActiveFilters) return@launch

            _uiState.value = state.copy(isSearching = true)
            val all = transactionRepository.getAllWithRelations(limit = 200)
            val filtered = all.filter { tx ->
                val t = tx.transaction
                val matchesKeyword = keyword == null ||
                    (t.merchantName?.contains(keyword, ignoreCase = true) == true) ||
                    (t.note?.contains(keyword, ignoreCase = true) == true)
                val matchesDate = start == null || t.timestamp >= start
                val matchesDateEnd = end == null || t.timestamp <= end
                matchesKeyword && matchesDate && matchesDateEnd
            }
            _uiState.value = _uiState.value.copy(results = filtered, isSearching = false, hasSearched = true)
        }
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filters = SearchFilters())
    }
}
