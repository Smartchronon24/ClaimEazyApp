package com.example.insuranceapp.ui.roles

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.Role
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class RoleState {
    object Idle : RoleState()
    object Loading : RoleState()
    data class Success(val roles: List<Role>) : RoleState()
    data class Error(val message: String) : RoleState()
}

class RoleViewModel(private val repository: InsuranceRepository) : ViewModel() {

    var state by mutableStateOf<RoleState>(RoleState.Idle)
        private set

    var roleDetail by mutableStateOf<Role?>(null)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }

    init {
        loadRoles()
    }

    fun loadRoles() {
        viewModelScope.launch {
            state = RoleState.Loading
            repository.getAllRoles()
                .onSuccess { state = RoleState.Success(it) }
                .onFailure { state = RoleState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getRole(roleId: Int) {
        viewModelScope.launch {
            repository.getRole(roleId)
                .onSuccess { roleDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading role")) }
        }
    }

    fun createRole(role: Role) {
        viewModelScope.launch {
            repository.createRole(role)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Role created successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadRoles()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating role")) }
        }
    }

    fun updateRole(roleId: Int, role: Role) {
        viewModelScope.launch {
            repository.updateRole(roleId, role)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Role updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadRoles()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating role")) }
        }
    }

    fun deleteRole(roleId: Int) {
        viewModelScope.launch {
            repository.deleteRole(roleId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Role deleted successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadRoles()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting role")) }
        }
    }

    class Factory(private val repository: InsuranceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RoleViewModel(repository) as T
        }
    }
}
