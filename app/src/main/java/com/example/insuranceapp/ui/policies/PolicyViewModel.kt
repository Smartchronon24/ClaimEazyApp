package com.example.insuranceapp.ui.policies

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.Policy
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class PolicyState {
    object Idle : PolicyState()
    object Loading : PolicyState()
    data class Success(val policies: List<Policy>) : PolicyState()
    data class Error(val message: String) : PolicyState()
}

class PolicyViewModel(private val repository: InsuranceRepository) : ViewModel() {

    var state by mutableStateOf<PolicyState>(PolicyState.Idle)
        private set

    var policyDetail by mutableStateOf<Policy?>(null)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }

    init {
        loadPolicies()
    }

    fun loadPolicies() {
        viewModelScope.launch {
            state = PolicyState.Loading
            repository.getAllPolicies()
                .onSuccess { state = PolicyState.Success(it) }
                .onFailure { state = PolicyState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getPolicy(policyId: String) {
        viewModelScope.launch {
            repository.getPolicy(policyId)
                .onSuccess { policyDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading policy")) }
        }
    }

    fun createPolicy(policy: Policy) {
        viewModelScope.launch {
            repository.createPolicy(policy)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Policy created successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPolicies()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating policy")) }
        }
    }

    fun updatePolicy(policyId: String, policy: Policy) {
        viewModelScope.launch {
            repository.updatePolicy(policyId, policy)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Policy updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPolicies()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating policy")) }
        }
    }

    fun deletePolicy(policyId: String) {
        viewModelScope.launch {
            repository.deletePolicy(policyId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Policy deleted successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPolicies()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting policy")) }
        }
    }

    class Factory(private val repository: InsuranceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PolicyViewModel(repository) as T
        }
    }
}
