package com.example.insuranceapp.ui.users

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.insuranceapp.data.model.UserAccount
import com.example.insuranceapp.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import com.example.insuranceapp.data.model.LoginRequest

sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Success(val users: List<UserAccount>) : UserState()
    data class Error(val message: String) : UserState()
}

class UserAccountViewModel(
    private val repository: InsuranceRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    var state by mutableStateOf<UserState>(UserState.Idle)
        private set

    var userDetail by mutableStateOf<UserAccount?>(null)
        private set

    var linkedCustomer by mutableStateOf<com.example.insuranceapp.data.model.Customer?>(null)
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
        object LoginSuccess : UiEvent()
        data class SignupSuccess(val userId: String) : UiEvent()
    }

    // Authentication State
    var isLoggedIn by mutableStateOf(prefs.getBoolean("is_logged_in", false))
        private set

    var currentUserId by mutableStateOf(prefs.getString("user_id", null))
        private set

    var currentRole by mutableStateOf(prefs.getString("user_role", null))
        private set

    val appRole: com.example.insuranceapp.data.model.AppRole
        get() = com.example.insuranceapp.data.model.AppRole.fromString(currentRole)

    val currentCustId: String?
        get() = prefs.getString("customer_id", null)

    var isGuest by mutableStateOf(prefs.getBoolean("is_guest", false))
        private set

    // UI Persistence State
    var selectedExplorerOption by mutableStateOf<String?>(null)

    init {
        if (isLoggedIn && currentUserId != null) {
            getUser(currentUserId!!)
            fetchUserContext(currentUserId!!)
        }
    }

    fun refreshCurrentUserData() {
        currentUserId?.let { userId ->
            getUser(userId)
            val custId = prefs.getString("customer_id", null)
            if (custId != null) {
                fetchLinkedCustomer(custId)
            } else {
                fetchUserContext(userId)
            }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            state = UserState.Loading
            repository.login(request)
                .onSuccess { response ->
                    if (response != null) {
                        isLoggedIn = true
                        isGuest = false
                        currentUserId = response.user_id
                        currentRole = response.role
                        selectedExplorerOption = null // Reset on new session
                        
                        prefs.edit().apply {
                            putBoolean("is_logged_in", true)
                            putBoolean("is_guest", false)
                            putString("user_id", response.user_id)
                            putString("user_role", response.role)
                            putString("customer_id", response.customer_id)
                            apply()
                        }
                        
                        // Fetch full user details, context and customer details if available
                        getUser(response.user_id)
                        if (response.customer_id != null) {
                            fetchLinkedCustomer(response.customer_id)
                        } else {
                            fetchUserContext(response.user_id)
                        }

                        state = UserState.Idle // Reset state after success
                        _eventFlow.emit(UiEvent.LoginSuccess)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Login successful"))
                    } else {
                        state = UserState.Error("Invalid response from server")
                    }
                }
                .onFailure {
                    state = UserState.Error(it.localizedMessage ?: "Login failed")
                    _eventFlow.emit(UiEvent.ShowSnackbar("Login failed: ${it.localizedMessage}"))
                }
        }
    }

    fun skipLogin() {
        viewModelScope.launch {
            state = UserState.Idle // Ensure idle
            isGuest = true
            isLoggedIn = false
            currentUserId = null
            currentRole = "GUEST"
            linkedCustomer = null
            selectedExplorerOption = null // Reset on new session
            
            prefs.edit().apply {
                putBoolean("is_guest", true)
                putBoolean("is_logged_in", false)
                putString("user_role", "GUEST")
                remove("user_id")
                remove("customer_id")
                apply()
            }
            
            _eventFlow.emit(UiEvent.LoginSuccess)
        }
    }

    fun logout() {
        viewModelScope.launch {
            state = UserState.Idle
            isLoggedIn = false
            isGuest = false
            currentUserId = null
            currentRole = null
            linkedCustomer = null
            userDetail = null
            selectedExplorerOption = null
            
            prefs.edit().apply {
                remove("is_logged_in")
                remove("user_id")
                remove("user_role")
                remove("customer_id")
                remove("is_guest")
                apply()
            }
        }
    }

    fun fetchUserContext(identifier: String) {
        viewModelScope.launch {
            repository.getUserContext(identifier)
                .onSuccess { context ->
                    if (context?.customer_id != null) {
                        fetchLinkedCustomer(context.customer_id)
                    }
                }
        }
    }

    fun fetchLinkedCustomer(custId: String) {
        viewModelScope.launch {
            repository.getCustomer(custId)
                .onSuccess { linkedCustomer = it }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            state = UserState.Loading
            repository.getAllUsers()
                .onSuccess { state = UserState.Success(it) }
                .onFailure { state = UserState.Error(it.localizedMessage ?: "Unknown Error") }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            repository.getUser(userId)
                .onSuccess { userDetail = it }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error loading user")) }
        }
    }

    fun createUser(user: UserAccount) {
        viewModelScope.launch {
            repository.createUser(user)
                .onSuccess { result ->
                    val userId = result?.get("user_id") ?: result?.get("userId") ?: "Unknown"
                    _eventFlow.emit(UiEvent.ShowSnackbar("User created successfully"))
                    _eventFlow.emit(UiEvent.SignupSuccess(userId))
                    loadUsers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error creating user")) }
        }
    }

    fun updateUser(userId: String, user: UserAccount) {
        viewModelScope.launch {
            repository.updateUser(userId, user)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User updated successfully"))
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadUsers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error updating user")) }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
                .onSuccess {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User deleted successfully"))
                    if (userId == currentUserId) {
                        logout()
                    }
                    _eventFlow.emit(UiEvent.NavigateBack)
                    loadUsers()
                }
                .onFailure { _eventFlow.emit(UiEvent.ShowSnackbar(it.localizedMessage ?: "Error deleting user")) }
        }
    }

    class Factory(
        private val repository: InsuranceRepository,
        private val prefs: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserAccountViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserAccountViewModel(repository, prefs) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
