package com.seamless.bookkeeper.presentation.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.domain.repository.AccountRepository
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import com.seamless.bookkeeper.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class AddTransactionUiState(
    val amount: String = "",
    val type: String = "EXPENSE",
    val selectedCategory: CategoryEntity? = null,
    val selectedAccount: AccountEntity? = null,
    val merchantName: String = "",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val categories: List<CategoryEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val isSaving: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val expenseCategories = categoryRepository.getByType("EXPENSE")
            val accounts = accountRepository.getAllEnabled()
            _uiState.value = _uiState.value.copy(
                categories = expenseCategories,
                accounts = accounts,
                selectedCategory = expenseCategories.firstOrNull(),
                selectedAccount = accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull()
            )
        }
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun setType(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun setCategory(category: CategoryEntity) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setAccount(account: AccountEntity) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
    }

    fun setMerchantName(name: String) {
        _uiState.value = _uiState.value.copy(merchantName = name)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun save(callback: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.amount.toBigDecimalOrNull() ?: return@launch

            transactionRepository.insert(TransactionEntity(
                amount = amount,
                type = state.type,
                categoryId = state.selectedCategory?.id,
                accountId = state.selectedAccount?.id ?: return@launch,
                merchantName = state.merchantName.ifBlank { null },
                note = state.note.ifBlank { null },
                timestamp = state.timestamp,
                source = "MANUAL",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ))
            callback()
        }
    }
}
