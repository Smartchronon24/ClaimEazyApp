package com.example.insuranceapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.insuranceapp.data.api.RetrofitClient
import com.example.insuranceapp.data.repository.InsuranceRepository
import com.example.insuranceapp.data.repository.StatsRepository
import com.example.insuranceapp.ui.claims.ClaimViewModel
import com.example.insuranceapp.ui.customers.CustomerViewModel
import com.example.insuranceapp.ui.policies.PolicyViewModel
import com.example.insuranceapp.ui.payments.PaymentViewModel
import com.example.insuranceapp.ui.users.UserAccountViewModel
import com.example.insuranceapp.ui.roles.RoleViewModel
import com.example.insuranceapp.ui.navigation.NavGraph
import com.example.insuranceapp.ui.theme.InsuranceAppTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Initialize RetrofitClient with context for SharedPreferences
        RetrofitClient.init(this)

        // Initialize Notification Channel
        com.example.insuranceapp.utils.NotificationHelper.createNotificationChannel(this)

        // Manual Dependency Injection
        val apiService = RetrofitClient.apiService
        val repository = InsuranceRepository(apiService)
        val statsRepository = StatsRepository()

        // ViewModel Initialization
        val customerViewModel = ViewModelProvider(
            this,
            CustomerViewModel.Factory(repository)
        )[CustomerViewModel::class.java]

        val claimViewModel = ViewModelProvider(
            this,
            ClaimViewModel.Factory(repository)
        )[ClaimViewModel::class.java]

        val policyViewModel = ViewModelProvider(
            this,
            PolicyViewModel.Factory(repository)
        )[PolicyViewModel::class.java]

        val paymentViewModel = ViewModelProvider(
            this,
            PaymentViewModel.Factory(repository)
        )[PaymentViewModel::class.java]

        val homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(repository, statsRepository)
        )[HomeViewModel::class.java]

        val quickStatsViewModel = ViewModelProvider(
            this,
            QuickStatsViewModel.Factory(statsRepository)
        )[QuickStatsViewModel::class.java]

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val userViewModel = ViewModelProvider(
            this,
            UserAccountViewModel.Factory(repository, prefs)
        )[UserAccountViewModel::class.java]

        val roleViewModel = ViewModelProvider(
            this,
            RoleViewModel.Factory(repository)
        )[RoleViewModel::class.java]

        val themeViewModel = ViewModelProvider(
            this,
            ThemeViewModel.Factory(AppTheme.SYSTEM) // Default to System
        )[ThemeViewModel::class.java]

        setContent {
            val themeMode by themeViewModel.themeMode
            val systemDark = isSystemInDarkTheme()
            
            val isDarkMode = when (themeMode) {
                AppTheme.SYSTEM -> systemDark
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            InsuranceAppTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                // Event Handling for Customers
                LaunchedEffect(Unit) {
                    customerViewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is CustomerViewModel.UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                            is CustomerViewModel.UiEvent.NavigateBack -> {
                                // Handled by individual screens to prevent double-pop 
                                // which causes the white-screen bug.
                            }
                            else -> {}
                        }
                    }
                }

                // Event Handling for Claims
                LaunchedEffect(Unit) {
                    claimViewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is ClaimViewModel.UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                            is ClaimViewModel.UiEvent.NavigateBack -> {
                                // Handled by individual screens to prevent double-pop.
                            }
                        }
                    }
                }

                // Event Handling for Users
                LaunchedEffect(Unit) {
                    userViewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is UserAccountViewModel.UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                            else -> {}
                        }
                    }
                }

                // Event Handling for Roles
                LaunchedEffect(Unit) {
                    roleViewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is RoleViewModel.UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                            else -> {}
                        }
                    }
                }

                // Event Handling for Home (Connection Status)
                LaunchedEffect(Unit) {
                    homeViewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is HomeViewModel.UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = androidx.compose.ui.Modifier.padding(
                            bottom = innerPadding.calculateBottomPadding()
                        ),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            themeViewModel = themeViewModel,
                            customerViewModel = customerViewModel,
                            claimViewModel = claimViewModel,
                            policyViewModel = policyViewModel,
                            paymentViewModel = paymentViewModel,
                            userViewModel = userViewModel,
                            roleViewModel = roleViewModel,
                            quickStatsViewModel = quickStatsViewModel,
                            isDark = isDarkMode
                        )
                    }
                }
            }
        }
    }
}
