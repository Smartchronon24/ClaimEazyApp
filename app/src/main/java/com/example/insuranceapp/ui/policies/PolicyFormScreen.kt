package com.example.insuranceapp.ui.policies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Policy
import com.example.insuranceapp.ui.components.AppDatePicker
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyFormScreen(
    policyId: String? = null,
    viewModel: PolicyViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val isEdit = policyId != null

    var policyType by remember { mutableStateOf("") }
    var premiumAmount by remember { mutableStateOf("") }
    var coverageAmount by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var typeError by remember { mutableStateOf<String?>(null) }
    var premiumError by remember { mutableStateOf<String?>(null) }
    var coverageError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(policyId) {
        if (isEdit && policyId != null) {
            viewModel.getPolicy(policyId)
        }
    }

    LaunchedEffect(viewModel.policyDetail) {
        if (isEdit && viewModel.policyDetail != null) {
            val policy = viewModel.policyDetail!!
            policyType = policy.policy_type
            premiumAmount = policy.premium.toString()
            coverageAmount = policy.coverage_amount.toString()
            startDate = policy.start_date
            endDate = policy.end_date
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is PolicyViewModel.UiEvent.NavigateBack -> {
                    onBack()
                }
                is PolicyViewModel.UiEvent.ShowSnackbar -> {
                    // Could handle snackbar here if passed host state
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEdit) "Edit Policy" else "Create Policy") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AppTextField(
                    value = policyType,
                    onValueChange = { policyType = it; typeError = null },
                    label = "Policy Type",
                    isError = typeError != null,
                    errorMessage = typeError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = premiumAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) premiumAmount = it; premiumError = null
                    },
                    label = "Premium Amount ($)",
                    isError = premiumError != null,
                    errorMessage = premiumError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = coverageAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) coverageAmount = it; coverageError = null
                    },
                    label = "Coverage Amount ($)",
                    isError = coverageError != null,
                    errorMessage = coverageError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppDatePicker(
                    label = "Start Date",
                    selectedDate = startDate,
                    onDateSelected = { startDate = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppDatePicker(
                    label = "End Date",
                    selectedDate = endDate,
                    onDateSelected = { endDate = it }
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (policyType.isBlank()) {
                            typeError = "Policy type is required"; isValid = false
                        }
                        val premInt = premiumAmount.toIntOrNull()
                        if (premInt == null || premInt < 0) {
                            premiumError = "Invalid premium amount"; isValid = false
                        }
                        val covInt = coverageAmount.toIntOrNull()
                        if (covInt == null || covInt < 0) {
                            coverageError = "Invalid coverage amount"; isValid = false
                        }

                        if (isValid) {
                            val policy = Policy(
                                policy_id = if (isEdit) policyId else null,
                                policy_type = policyType,
                                premium = premInt!!,
                                coverage_amount = covInt!!,
                                start_date = startDate,
                                end_date = endDate
                            )

                            if (isEdit && policyId != null) {
                                viewModel.updatePolicy(policyId, policy)
                            } else {
                                viewModel.createPolicy(policy)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEdit) "Update Policy" else "Save Policy")
                }
            }
        }
    }
}
