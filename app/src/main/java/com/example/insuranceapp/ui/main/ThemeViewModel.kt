package com.example.insuranceapp.ui.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

class ThemeViewModel(initialTheme: AppTheme = AppTheme.SYSTEM) : ViewModel() {

    private val _themeMode = mutableStateOf(initialTheme)
    val themeMode: State<AppTheme> = _themeMode

    fun setThemeMode(mode: AppTheme) {
        _themeMode.value = mode
    }

    class Factory(private val initialTheme: AppTheme) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ThemeViewModel(initialTheme) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
