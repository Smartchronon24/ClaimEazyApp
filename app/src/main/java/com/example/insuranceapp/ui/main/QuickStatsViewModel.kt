package com.example.insuranceapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.AdminInsights
import com.example.insuranceapp.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuickStatsUiState {
    data object Loading : QuickStatsUiState()
    data class Success(val insights: AdminInsights) : QuickStatsUiState()
    data class Error(val message: String) : QuickStatsUiState()
}

class QuickStatsViewModel(private val repository: StatsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<QuickStatsUiState>(QuickStatsUiState.Loading)
    val uiState: StateFlow<QuickStatsUiState> = _uiState.asStateFlow()

    init {
        fetchStats()
    }

    private fun fetchStats() {
        viewModelScope.launch {
            repository.getAdminInsights().collect { result ->
                result.onSuccess { insights ->
                    _uiState.value = QuickStatsUiState.Success(insights)
                }.onFailure { error ->
                    _uiState.value = QuickStatsUiState.Error(error.message ?: "Unknown error")
                }
            }
        }
    }

    class Factory(private val repository: StatsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuickStatsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuickStatsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
