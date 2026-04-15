package com.example.insuranceapp.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Payment
import com.example.insuranceapp.ui.components.AppDatePicker
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    paymentId: String? = null,
    viewModel: PaymentViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val isEdit = paymentId != null

    var policyId by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("Completed") }
    var paymentModeExpanded by remember { mutableStateOf(false) }
    var showCardPrompt by remember { mutableStateOf(false) }

    var policyIdError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var modeError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(paymentId) {
        if (isEdit && paymentId != null) {
            viewModel.getPayment(paymentId)
        }
    }

    LaunchedEffect(viewModel.paymentDetail) {
        if (isEdit && viewModel.paymentDetail != null) {
            val payment = viewModel.paymentDetail!!
            policyId = payment.policy_id
            paymentAmount = payment.payment_amount.toString()
            paymentDate = payment.payment_date
            paymentMode = payment.payment_mode
            paymentStatus = payment.payment_status
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is PaymentViewModel.UiEvent.NavigateBack -> {
                    onBack()
                }
                is PaymentViewModel.UiEvent.ShowSnackbar -> {
                    // Could handle snackbar logic 
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEdit) "Edit Payment" else "Create Payment") },
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
                    value = policyId,
                    onValueChange = { policyId = it; policyIdError = null },
                    label = "Policy ID",
                    isError = policyIdError != null,
                    errorMessage = policyIdError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = paymentAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) paymentAmount = it; amountError = null
                    },
                    label = "Payment Amount (₹)",
                    isError = amountError != null,
                    errorMessage = amountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppDatePicker(
                    label = "Payment Date",
                    selectedDate = paymentDate,
                    onDateSelected = { paymentDate = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Payment Mode Dropdown
                Text("Payment Mode", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = paymentModeExpanded,
                    onExpandedChange = { paymentModeExpanded = !paymentModeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Payment Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentModeExpanded) },
                        isError = modeError != null,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = paymentModeExpanded,
                        onDismissRequest = { paymentModeExpanded = false }
                    ) {
                        val modes = listOf("UPI", "NetBanking", "Cards", "Crypto", "Cash")
                        modes.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode) },
                                onClick = {
                                    if (mode == "Cards") {
                                        showCardPrompt = true
                                    } else {
                                        paymentMode = mode
                                        modeError = null
                                    }
                                    paymentModeExpanded = false
                                }
                            )
                        }
                    }
                }
                if (modeError != null) {
                    Text(
                        text = modeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (showCardPrompt) {
                    AlertDialog(
                        onDismissRequest = { showCardPrompt = false },
                        title = { Text("Select Card Type") },
                        text = { Text("Is this a Credit Card or a Debit Card?") },
                        confirmButton = {
                            TextButton(onClick = {
                                paymentMode = "Credit Card"
                                modeError = null
                                showCardPrompt = false
                            }) {
                                Text("Credit Card")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                paymentMode = "Debit Card"
                                modeError = null
                                showCardPrompt = false
                            }) {
                                Text("Debit Card")
                            }
                        }
                    )
                }

                Text("Status", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Completed", "Pending", "Failed").forEach { s ->
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(selected = paymentStatus == s, onClick = { paymentStatus = s })
                            Text(s, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (policyId.isBlank()) {
                            policyIdError = "Policy ID is required"; isValid = false
                        }
                        if (paymentMode.isBlank()) {
                            modeError = "Payment mode is required"; isValid = false
                        }
                        val amtInt = paymentAmount.toIntOrNull()
                        if (amtInt == null || amtInt <= 0) {
                            amountError = "Invalid payment amount"; isValid = false
                        }

                        if (isValid) {
                            val payment = Payment(
                                payment_id = if (isEdit) paymentId else null,
                                policy_id = policyId,
                                payment_amount = amtInt!!,
                                payment_date = paymentDate,
                                payment_mode = paymentMode,
                                payment_status = paymentStatus
                            )

                            if (isEdit && paymentId != null) {
                                viewModel.updatePayment(paymentId, payment)
                            } else {
                                viewModel.createPayment(payment)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEdit) "Update Payment" else "Save Payment")
                }
            }
        }
    }
}
