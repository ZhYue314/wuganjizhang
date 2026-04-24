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
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    // 编辑模式相关
    val isEditMode: Boolean = false,
    val editingTransactionId: Int = 0
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
                // 预加载所有类型的分类列表
                categoryDao.getCategoriesByType("expense").observeForever { categories ->
                    _uiState.value = _uiState.value.copy(
                        expenseCategories = categories ?: emptyList(),
                        categories = categories ?: emptyList(), // 默认显示支出分类
                        isLoading = false
                    )
                }
                
                categoryDao.getCategoriesByType("income").observeForever { categories ->
                    _uiState.value = _uiState.value.copy(
                        incomeCategories = categories ?: emptyList()
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
    
    // 数字键盘输入处理
    fun appendDigit(digit: String) {
        val currentAmount = _uiState.value.amount
        
        // 防止多个小数点
        if (digit == "." && currentAmount.contains(".")) {
            return
        }
        
        // 防止开头多个0
        if (currentAmount == "0" && digit != ".") {
            _uiState.value = _uiState.value.copy(amount = digit)
            return
        }
        
        // 限制小数点后最多2位
        if (currentAmount.contains(".")) {
            val decimalPart = currentAmount.split(".")[1]
            if (decimalPart.length >= 2) {
                return
            }
        }
        
        _uiState.value = _uiState.value.copy(amount = currentAmount + digit)
    }
    
    fun deleteLastDigit() {
        val currentAmount = _uiState.value.amount
        if (currentAmount.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                amount = currentAmount.dropLast(1)
            )
        }
    }

    fun updateType(type: String) {
        // 切换类型时清除所有错误
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            selectedCategory = null, // 切换类型时清空分类选择
            error = null // 清除错误提示
        )
        // 不再需要重新加载分类，因为已经预加载了
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
        if (_uiState.value.isEditMode) {
            updateTransaction()
        } else {
            createTransaction()
        }
    }
    
    private fun createTransaction() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                
                // 验证必填字段
                if (state.amount.isEmpty() || state.amount.toDoubleOrNull() == null) {
                    _uiState.value = state.copy(error = "请输入有效金额")
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
        val currentExpenseCategories = _uiState.value.expenseCategories
        val currentIncomeCategories = _uiState.value.incomeCategories
        
        // 重置表单，但保留账户列表和分类列表
        _uiState.value = AddTransactionUiState(
            selectedType = "expense", // 重置为支出类型
            selectedAccount = currentAccount,
            accounts = currentAccounts,
            expenseCategories = currentExpenseCategories,
            incomeCategories = currentIncomeCategories,
            categories = currentExpenseCategories // 默认显示支出分类
        )
    }
    
    // 加载交易数据进行编辑
    fun loadTransactionForEdit(transaction: Transaction) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEditMode = true,
                editingTransactionId = transaction.id,
                amount = String.format("%.2f", transaction.amount),
                selectedType = transaction.type,
                merchant = transaction.merchant ?: "",
                remark = transaction.remark ?: "",
                timestamp = transaction.timestamp,
                error = null
            )
            
            // 设置分类
            if (transaction.categoryId > 0) {
                val categoryList = if (transaction.type == "expense") {
                    _uiState.value.expenseCategories
                } else {
                    _uiState.value.incomeCategories
                }
                val category = categoryList.find { it.id == transaction.categoryId }
                _uiState.value = _uiState.value.copy(selectedCategory = category)
            }
            
            // 设置账户
            val account = _uiState.value.accounts.find { it.id == transaction.accountId }
            _uiState.value = _uiState.value.copy(selectedAccount = account)
        }
    }
    
    // 更新交易
    fun updateTransaction() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                
                // 验证必填字段
                if (state.amount.isEmpty() || state.amount.toDoubleOrNull() == null) {
                    _uiState.value = state.copy(error = "请输入有效金额")
                    return@launch
                }
                
                if (state.selectedAccount == null) {
                    _uiState.value = state.copy(error = "请选择账户")
                    return@launch
                }

                val amount = state.amount.toDouble()
                val oldTransaction = transactionDao.getTransactionById(state.editingTransactionId)
                
                if (oldTransaction == null) {
                    _uiState.value = state.copy(error = "交易记录不存在")
                    return@launch
                }
                
                // 创建更新后的交易记录
                val updatedTransaction = oldTransaction.copy(
                    type = state.selectedType,
                    amount = amount,
                    categoryId = state.selectedCategory?.id ?: 0,
                    categoryName = state.selectedCategory?.name,
                    accountId = state.selectedAccount!!.id,
                    accountName = state.selectedAccount!!.name,
                    merchant = if (state.merchant.isNotBlank()) state.merchant else null,
                    timestamp = state.timestamp,
                    remark = if (state.remark.isNotBlank()) state.remark else null,
                    updatedAt = System.currentTimeMillis()
                )

                // 更新数据库
                transactionDao.update(updatedTransaction)
                
                // 更新账户余额（计算差额）
                val oldAmount = oldTransaction.amount
                val amountDiff = amount - oldAmount
                
                val newBalance = if (state.selectedType == "expense" || state.selectedType == "transfer") {
                    state.selectedAccount!!.balance - amountDiff
                } else {
                    state.selectedAccount!!.balance + amountDiff
                }
                accountDao.updateBalance(state.selectedAccount!!.id, newBalance)

                _uiState.value = state.copy(isSuccess = true, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
