package com.example.insuranceapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.insuranceapp.data.repository.StatsRepository

class HomeViewModel(
    private val repository: InsuranceRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _welcomeMessage = MutableStateFlow<String>("Welcome...")
    val welcomeMessage: StateFlow<String> = _welcomeMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectionKey = MutableStateFlow<String?>(com.example.insuranceapp.data.api.RetrofitClient.getNgrokKey())
    val connectionKey: StateFlow<String?> = _connectionKey.asStateFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val currentConnectionKey: String?
        get() = com.example.insuranceapp.data.api.RetrofitClient.getNgrokKey()

    init {
        fetchWelcomeMessage()
        // F-Droid build: Connection key is managed manually via HomeScreen UI.
        // Firebase auto-sync has been removed for FOSS compliance.
    }

    private fun parseNgrokKey(input: String): String {
        // Regex to extract identifier from formats like:
        // https://xyz.ngrok-free.app
        // xyz.ngrok-free.app
        // xyz
        val regex = Regex("(?:https://)?([^.]+)(?:\\.ngrok-free\\.app)?")
        val matchResult = regex.find(input)
        return matchResult?.groupValues?.get(1) ?: input
    }

    fun updateConnectionKey(key: String) {
        com.example.insuranceapp.data.api.RetrofitClient.updateNgrokKey(key)
        _connectionKey.value = key
        fetchWelcomeMessage(isManualUpdate = true)
    }

    fun fetchWelcomeMessage(isManualUpdate: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getWelcomeMessage()
                .onSuccess {
                    _welcomeMessage.value = it
                    _isConnected.value = true
                    if (isManualUpdate) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Successfully connected!"))
                    }
                }
                .onFailure {
                    _welcomeMessage.value = "Insurance Manager  Offline Mode" // Fallback
                    _isConnected.value = false
                    if (isManualUpdate) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Unable to connect to Server: Check the key or wait for server to come online"))
                    }
                }
            _isLoading.value = false
        }
    }

    class Factory(
        private val repository: InsuranceRepository,
        private val statsRepository: StatsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository, statsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
