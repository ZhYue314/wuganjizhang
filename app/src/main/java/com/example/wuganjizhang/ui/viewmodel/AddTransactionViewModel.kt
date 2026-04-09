package com.example.wuganjizhang.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wuganjizhang.data.local.dao.AccountDao
import com.example.wuganjizhang.data.local.dao.CategoryDao
import com.example.wuganjizhang.data.local.dao.TransactionDao
import com.example.wuganjizhang.model.Account
import com.example.wuganjizhang.model.Category
import com.example.wuganjizhang.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val amount: String = "",
    val selectedType: String = "expense", // expense, income, transfer
    val selectedCategory: Category? = null,
    val selectedAccount: Account? = null,
    val merchant: String = "",
    val remark: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 加载分类列表
                categoryDao.getCategoriesByType("expense").observeForever { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories ?: emptyList(),
                        isLoading = false
                    )
                }

                // 加载账户列表
                accountDao.getEnabledAccountsLive().observeForever { accounts ->
                    _uiState.value = _uiState.value.copy(
                        accounts = accounts ?: emptyList()
                    )
                    // 默认选择第一个账户
                    if (accounts?.isNotEmpty() == true && _uiState.value.selectedAccount == null) {
                        _uiState.value = _uiState.value.copy(selectedAccount = accounts[0])
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateAmount(amount: String) {
        val currentState = _uiState.value
        // 如果用户开始输入，清除金额错误
        val newError = if (currentState.error == "请输入有效金额" && amount.isNotEmpty()) {
            null
        } else {
            currentState.error
        }
        _uiState.value = currentState.copy(
            amount = amount,
            error = newError
        )
    }

    fun updateType(type: String) {
        // 切换类型时清除所有错误
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            selectedCategory = null, // 切换类型时清空分类选择
            error = null // 清除错误提示
        )
        // 重新加载对应类型的分类
        viewModelScope.launch {
            categoryDao.getCategoriesByType(type).observeForever { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories ?: emptyList()
                )
            }
        }
    }

    fun selectCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun selectAccount(account: Account) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
    }

    fun updateMerchant(merchant: String) {
        _uiState.value = _uiState.value.copy(merchant = merchant)
    }

    fun updateRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun updateTimestamp(timestamp: Long) {
        _uiState.value = _uiState.value.copy(timestamp = timestamp)
    }

    fun saveTransaction() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                
                // 验证必填字段
                if (state.amount.isEmpty() || state.amount.toDoubleOrNull() == null) {
                    _uiState.value = state.copy(error = "请输入有效金额")
                    return@launch
                }
                
                // 转账不需要分类，其他类型需要
                if (state.selectedType != "transfer" && state.selectedCategory == null) {
                    _uiState.value = state.copy(error = "请选择分类")
                    return@launch
                }
                
                if (state.selectedAccount == null) {
                    _uiState.value = state.copy(error = "请选择账户")
                    return@launch
                }

                val amount = state.amount.toDouble()
                
                // 创建交易记录
                val transaction = Transaction(
                    type = state.selectedType,
                    amount = amount,
                    categoryId = state.selectedCategory?.id ?: 0,
                    categoryName = state.selectedCategory?.name,
                    accountId = state.selectedAccount!!.id,
                    accountName = state.selectedAccount!!.name,
                    merchant = if (state.merchant.isNotBlank()) state.merchant else null,
                    timestamp = state.timestamp,
                    remark = if (state.remark.isNotBlank()) state.remark else null,
                    source = "manual"
                )

                // 插入数据库
                transactionDao.insert(transaction)
                
                // 更新账户余额（转账也是支出）
                val newBalance = if (state.selectedType == "expense" || state.selectedType == "transfer") {
                    state.selectedAccount!!.balance - amount
                } else {
                    state.selectedAccount!!.balance + amount
                }
                accountDao.updateBalance(state.selectedAccount!!.id, newBalance)

                _uiState.value = state.copy(isSuccess = true, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetForm() {
        val currentAccount = _uiState.value.selectedAccount
        val currentAccounts = _uiState.value.accounts
        // 重置表单，但保留账户列表，重新加载默认类型（支出）的分类
        _uiState.value = AddTransactionUiState(
            selectedAccount = currentAccount,
            accounts = currentAccounts
        )
        // 重新加载支出分类（默认类型）
        viewModelScope.launch {
            categoryDao.getCategoriesByType("expense").observeForever { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories ?: emptyList()
                )
            }
        }
    }
}
