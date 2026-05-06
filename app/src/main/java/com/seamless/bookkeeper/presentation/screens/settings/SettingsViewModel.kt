package com.seamless.bookkeeper.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamless.bookkeeper.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppSettingsState(
    val isDarkMode: Boolean = false,
    val darkModeValue: String = "SYSTEM",
    val isAutoMode: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AppSettingsState())
    val state: StateFlow<AppSettingsState> = _state

    init {
        viewModelScope.launch {
            userPreferences.darkMode.collect { mode ->
                _state.value = _state.value.copy(
                    darkModeValue = mode,
                    isDarkMode = mode == "DARK"
                )
            }
        }
        viewModelScope.launch {
            userPreferences.recordMode.collect { mode ->
                _state.value = _state.value.copy(
                    isAutoMode = mode == "AUTO"
                )
            }
        }
    }

    fun cycleDarkMode() {
        viewModelScope.launch {
            val next = when (_state.value.darkModeValue) {
                "SYSTEM" -> "DARK"
                "DARK" -> "LIGHT"
                else -> "SYSTEM"
            }
            userPreferences.setDarkMode(next)
        }
    }

    fun toggleAutoMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setRecordMode(if (enabled) "AUTO" else "CONFIRM")
        }
    }
}
