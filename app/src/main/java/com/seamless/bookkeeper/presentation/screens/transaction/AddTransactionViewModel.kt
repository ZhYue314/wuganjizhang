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
    val allCategories: Map<String, List<CategoryEntity>> = emptyMap(),
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

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val allCats = mapOf(
                "EXPENSE" to categoryRepository.getByType("EXPENSE"),
                "INCOME" to categoryRepository.getByType("INCOME"),
                "TRANSFER" to emptyList<CategoryEntity>()
            )
            val accounts = accountRepository.getAllEnabled()
            val expenseCats = allCats["EXPENSE"] ?: emptyList()
            _uiState.value = _uiState.value.copy(
                categories = expenseCats,
                allCategories = allCats,
                accounts = accounts,
                selectedAccount = accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull()
            )
        }
    }

    companion object {
        val typeList = listOf("EXPENSE", "INCOME", "TRANSFER")
        fun getTypeForPage(page: Int) = typeList.getOrElse(page) { "EXPENSE" }
        fun getPageForType(type: String) = typeList.indexOf(type).coerceAtLeast(0)
    }

    fun setType(type: String) {
        val cats = _uiState.value.allCategories[type] ?: emptyList()
        val selected = if (cats.any { it.id == _uiState.value.selectedCategory?.id }) {
            _uiState.value.selectedCategory
        } else cats.firstOrNull()
        _uiState.value = _uiState.value.copy(type = type, categories = cats, selectedCategory = selected)
    }

    fun setPage(page: Int) {
        val type = getTypeForPage(page)
        if (type != _uiState.value.type) {
            setType(type)
        }
    }

    fun setCategory(category: CategoryEntity) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun appendDigit(digit: String) {
        val current = _uiState.value.amount
        if (digit == ".") {
            if (current.contains(".")) return
            _uiState.value = _uiState.value.copy(amount = if (current.isEmpty()) "0." else "$current.")
        } else {
            if (current.contains(".")) {
                val parts = current.split(".")
                if (parts.size == 2 && parts[1].length >= 2) return
            }
            val newAmount = current + digit
            if (newAmount.length > 12) return
            _uiState.value = _uiState.value.copy(amount = newAmount)
        }
    }

    fun deleteLastDigit() {
        val current = _uiState.value.amount
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(amount = current.dropLast(1))
        }
    }

    fun clearAmount() {
        _uiState.value = _uiState.value.copy(amount = "")
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val amount = state.amount.toBigDecimalOrNull() ?: return@launch
            if (amount <= BigDecimal.ZERO) return@launch
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
            onSuccess()
        }
    }
}
