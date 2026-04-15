package com.example.insuranceapp.ui.customers

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Customer
import com.example.insuranceapp.data.model.CustomerRequest
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.components.AppDropdown
import com.example.insuranceapp.data.model.LocationConstants
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    custId: String? = null,
    viewModel: CustomerViewModel,
    claimViewModel: com.example.insuranceapp.ui.claims.ClaimViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val isEdit = custId != null
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedClaims by remember { mutableStateOf(setOf<String>()) }
    var initialClaims by remember { mutableStateOf(setOf<String>()) }
    var expanded by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(custId) {
        if (isEdit && custId != null) {
            viewModel.getCustomer(custId)
        }
    }

    LaunchedEffect(viewModel.customerDetail, claimViewModel.state) {
        if (isEdit && viewModel.customerDetail != null) {
            val customer = viewModel.customerDetail!!
            name = customer.name
            phone = customer.phone.toString()
            email = customer.email.orEmpty()
            age = customer.age.toString()
            address = customer.address
            
            // Re-sync assigned claims from the global claims list because 
            // the customer object from the backend has an empty claims list.
            val claimsState = claimViewModel.state
            if (claimsState is com.example.insuranceapp.ui.claims.ClaimState.Success) {
                val assigned = claimsState.claims
                    .filter { it.customer_id == custId }
                    .mapNotNull { it.Claim_ID }
                    .toSet()
                selectedClaims = assigned
                initialClaims = assigned
            }
        }
    }

    LaunchedEffect(Unit) {
        claimViewModel.loadClaims() // Load all claims to filter assigned ones
        claimViewModel.loadUnassignedClaims()
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CustomerViewModel.UiEvent.Created -> {
                    val toAdd = selectedClaims
                    if (toAdd.isEmpty()) {
                        onBack()
                    } else {
                        isProcessing = true
                        var completedJobs = 0
                        toAdd.forEach { claimId ->
                            claimViewModel.assignClaim(claimId, event.customerId) {
                                completedJobs++
                                if (completedJobs == toAdd.size) {
                                    isProcessing = false
                                    onBack()
                                }
                            }
                        }
                    }
                }
                is CustomerViewModel.UiEvent.NavigateBack -> {
                    if (isEdit && custId != null) {
                        val toAdd = selectedClaims - initialClaims
                        val toRemove = initialClaims - selectedClaims
                        val totalJobs = toAdd.size + toRemove.size
                        
                        if (totalJobs == 0) {
                            onBack()
                        } else {
                            isProcessing = true
                            var completedJobs = 0
                            
                            toAdd.forEach { claimId ->
                                claimViewModel.assignClaim(claimId, custId) {
                                    completedJobs++
                                    if (completedJobs == totalJobs) {
                                        isProcessing = false
                                        onBack()
                                    }
                                }
                            }
                            toRemove.forEach { claimId ->
                                claimViewModel.deassignClaim(claimId) {
                                    completedJobs++
                                    if (completedJobs == totalJobs) {
                                        isProcessing = false
                                        onBack()
                                    }
                                }
                            }
                        }
                    } else {
                        onBack()
                    }
                }
                is CustomerViewModel.UiEvent.ShowSnackbar -> {}
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEdit) "Edit Customer" else "Create Customer") },
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
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = "Name",
                    isError = nameError != null,
                    errorMessage = nameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = phone,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) phone = it; phoneError = null
                    },
                    label = "Phone",
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = "Email Address",
                    isError = emailError != null,
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = age,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) age = it; ageError = null
                    },
                    label = "Age",
                    isError = ageError != null,
                    errorMessage = ageError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppDropdown(
                    label = "Address (State)",
                    options = LocationConstants.INDIAN_STATES,
                    selectedOption = if (address.isBlank()) "Select State" else address,
                    onOptionSelected = { address = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Assigned Claims", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (claimId in selectedClaims) {
                        InputChip(
                            selected = true,
                            onClick = { selectedClaims = selectedClaims - claimId },
                            label = { Text("($claimId)") },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val unassignedData = claimViewModel.unassignedClaimsState
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "Select Claims to Link",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        label = { Text("Unassigned Claims") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        when (unassignedData) {
                            is com.example.insuranceapp.ui.claims.ClaimState.Loading -> {
                                DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
                            }
                            is com.example.insuranceapp.ui.claims.ClaimState.Success -> {
                                val available = unassignedData.claims.filter { it.Claim_ID !in selectedClaims }
                                if (available.isEmpty()) {
                                    DropdownMenuItem(text = { Text("No unassigned claims available") }, onClick = {})
                                } else {
                                    for (claim in available) {
                                        DropdownMenuItem(
                                            text = { Text("Claim #${claim.Claim_ID ?: "N/A"} (${claim.hospital_id})") },
                                            onClick = {
                                                claim.Claim_ID?.let { id ->
                                                    selectedClaims = selectedClaims + id
                                                    expanded = false
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            is com.example.insuranceapp.ui.claims.ClaimState.Error -> {
                                DropdownMenuItem(text = { Text("Error loading claims") }, onClick = {})
                            }
                            else -> {}
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (name.isBlank()) {
                            nameError = "Name is required"; isValid = false
                        }
                        if (phone.length < 10) {
                            phoneError = "Phone must be at least 10 digits"; isValid = false
                        }
                        if (email.isBlank() || !email.contains("@")) {
                            emailError = "Valid email is required"; isValid = false
                        }
                        val ageInt = age.toIntOrNull()
                        if (ageInt == null || ageInt <= 0) {
                            ageError = "Age must be a positive number"; isValid = false
                        }

                        if (isValid) {
                            val customerRequest = CustomerRequest(
                                name = name,
                                email = email,
                                phone = phone.toLong(),
                                age = ageInt!!,
                                address = address
                            )

                            if (isEdit && custId != null) {
                                viewModel.updateCustomer(custId, customerRequest)
                            } else {
                                viewModel.createCustomer(customerRequest)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEdit) "Update Customer" else "Save Customer")
                    }
                }
            }
        }
    }
}
