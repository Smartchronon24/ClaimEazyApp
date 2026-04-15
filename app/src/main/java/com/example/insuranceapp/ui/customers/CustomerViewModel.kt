package com.example.insuranceapp.ui.customers

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.Customer
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class CustomerState {
    object Idle : CustomerState()
    object Loading : CustomerState()
    data class Success(val customers: List<Customer>) : CustomerState()
    data class Error(val message: String) : CustomerState()
}

class CustomerViewModel(private val repository: InsuranceRepository) : ViewModel() {

    var state by mutableStateOf<CustomerState>(CustomerState.Idle)
        private set

    var customerDetail by mutableStateOf<Customer?>(null)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
        data class Created(val customerId: String) : UiEvent()
    }

    init {
        loadCustomers()
    }

    fun loadCustomers() {
        viewModelScope.launch {
            state = CustomerState.Loading
            repository.getAllCustomers()
                .onSuccess { state = CustomerState.Success(it) }
                .onFailure { state = CustomerState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getCustomer(customerId: String) {
        viewModelScope.launch {
            repository.getCustomer(customerId)
                .onSuccess { customerDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading customer")) }
        }
    }

    fun createCustomer(customer: com.example.insuranceapp.data.model.CustomerRequest) {
        viewModelScope.launch {
            repository.createCustomer(customer)
                .onSuccess {
                    val newId = it?.get("customer_id") ?: ""
                    _eventFlow.emit(UiEvent.ShowSnackbar("Customer created successfully"))
                    _eventFlow.emit(UiEvent.Created(newId))
                    loadCustomers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating customer")) }
        }
    }

    fun updateCustomer(customerId: String, customer: com.example.insuranceapp.data.model.CustomerRequest) {
        viewModelScope.launch {
            repository.updateCustomer(customerId, customer)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Customer updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadCustomers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating customer")) }
        }
    }

    fun deleteCustomer(customerId: String) {
        viewModelScope.launch {
            repository.deleteCustomer(customerId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Customer deleted successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadCustomers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting customer")) }
        }
    }

    class Factory(private val repository: InsuranceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CustomerViewModel(repository) as T
        }
    }
}
