package com.seamless.bookkeeper.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.local.entity.AccountEntity
import com.seamless.bookkeeper.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountManagementUiState(
    val accounts: List<AccountEntity> = emptyList()
)

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountManagementUiState())
    val uiState: StateFlow<AccountManagementUiState> = _uiState

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val accounts = accountRepository.getAllEnabled()
            _uiState.value = AccountManagementUiState(accounts = accounts)
        }
    }
}
