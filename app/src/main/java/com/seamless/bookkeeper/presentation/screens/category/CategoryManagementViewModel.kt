package com.seamless.bookkeeper.presentation.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.local.entity.CategoryEntity
import com.seamless.bookkeeper.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryManagementUiState(
    val presetCategories: List<CategoryEntity> = emptyList(),
    val customCategories: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = CategoryManagementUiState(
                presetCategories = categoryRepository.getPresetCategories(),
                customCategories = categoryRepository.getCustomCategories()
            )
        }
    }
}
