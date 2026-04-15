package com.example.insuranceapp.ui.payments

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.Payment
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val payments: List<Payment>) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(private val repository: InsuranceRepository) : ViewModel() {

    var state by mutableStateOf<PaymentState>(PaymentState.Idle)
        private set

    var paymentDetail by mutableStateOf<Payment?>(null)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }

    init {
        loadPayments()
    }

    fun loadPayments() {
        viewModelScope.launch {
            state = PaymentState.Loading
            repository.getAllPayments()
                .onSuccess { state = PaymentState.Success(it) }
                .onFailure { state = PaymentState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getPayment(paymentId: String) {
        viewModelScope.launch {
            repository.getPayment(paymentId)
                .onSuccess { paymentDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading payment")) }
        }
    }

    fun createPayment(payment: Payment) {
        viewModelScope.launch {
            repository.createPayment(payment)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Payment created successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPayments()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating payment")) }
        }
    }

    fun updatePayment(paymentId: String, payment: Payment) {
        viewModelScope.launch {
            repository.updatePayment(paymentId, payment)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Payment updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPayments()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating payment")) }
        }
    }

    fun deletePayment(paymentId: String) {
        viewModelScope.launch {
            repository.deletePayment(paymentId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Payment deleted successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadPayments()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting payment")) }
        }
    }

    class Factory(private val repository: InsuranceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(repository) as T
        }
    }
}
