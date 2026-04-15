package com.example.insuranceapp.ui.claims

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.Claim
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class ClaimState {
    object Idle : ClaimState()
    object Loading : ClaimState()
    data class Success(val claims: List<Claim>) : ClaimState()
    data class Error(val message: String) : ClaimState()
}

class ClaimViewModel(private val repository: InsuranceRepository) : ViewModel() {

    var state by mutableStateOf<ClaimState>(ClaimState.Idle)
        private set

    var claimDetail by mutableStateOf<Claim?>(null)
        private set

    var unassignedClaimsState by mutableStateOf<ClaimState>(ClaimState.Idle)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }

    init {
        loadClaims()
    }

    fun loadClaims() {
        viewModelScope.launch {
            state = ClaimState.Loading
            repository.getAllClaims()
                .onSuccess { state = ClaimState.Success(it) }
                .onFailure { state = ClaimState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getClaim(claimId: String) {
        viewModelScope.launch {
            repository.getClaim(claimId)
                .onSuccess { claimDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading claim")) }
        }
    }

    fun createClaim(claim: Claim) {
        viewModelScope.launch {
            repository.createClaim(claim)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Claim created successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadClaims()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating claim")) }
        }
    }

    fun updateClaim(claimId: String, claim: Claim) {
        viewModelScope.launch {
            repository.updateClaim(claimId, claim)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Claim updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadClaims()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating claim")) }
        }
    }

    fun deleteClaim(claimId: String) {
        viewModelScope.launch {
            repository.deleteClaim(claimId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Claim deleted successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadClaims()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting claim")) }
        }
    }

    fun loadUnassignedClaims() {
        viewModelScope.launch {
            unassignedClaimsState = ClaimState.Loading
            repository.getUnassignedClaims()
                .onSuccess { unassignedClaimsState = ClaimState.Success(it) }
                .onFailure { unassignedClaimsState = ClaimState.Error(it.localizedMessage ?: "Error loading unassigned claims") }
        }
    }

    fun assignClaim(claimId: String, customerId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.assignClaim(claimId, customerId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Claim assigned successfully"))
                    getClaim(claimId) // Refresh the detail for the current screen
                    loadClaims()
                    loadUnassignedClaims()
                    onComplete()
                }
                .onFailure { 
                    _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error assigning claim"))
                    onComplete() // Still complete to stop loading spinners
                }
        }
    }

    fun deassignClaim(claimId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deassignClaim(claimId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Claim deassigned successfully"))
                    getClaim(claimId) // Refresh detail
                    loadClaims()
                    onComplete()
                }
                .onFailure { 
                    _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deassigning claim")) 
                    onComplete() // Still complete to stop loading spinners
                }
        }
    }

    class Factory(private val repository: InsuranceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClaimViewModel(repository) as T
        }
    }
}
