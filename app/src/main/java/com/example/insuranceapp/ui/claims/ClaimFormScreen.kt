package com.example.insuranceapp.ui.claims

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
import com.example.insuranceapp.data.model.Claim
import com.example.insuranceapp.ui.policies.PolicyViewModel
import com.example.insuranceapp.ui.policies.PolicyState
import com.example.insuranceapp.ui.components.AppDatePicker
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import com.example.insuranceapp.ui.users.UserAccountViewModel
import com.example.insuranceapp.utils.HapticHelper
import androidx.compose.ui.platform.LocalContext
import com.example.insuranceapp.data.model.canEditClaimDetails
import com.example.insuranceapp.data.model.canEditClaimStatus
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimFormScreen(
    claimId: String? = null,
    viewModel: ClaimViewModel,
    policyViewModel: PolicyViewModel,
    userViewModel: UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val context = LocalContext.current
    val isEdit = claimId != null
    val appRole = userViewModel.appRole

    // State declarations must come before logic that uses them
    var policyId by remember { mutableStateOf("") }
    var selectedPolicyName by remember { mutableStateOf("Select Policy") }
    var policyDropdownExpanded by remember { mutableStateOf(false) }
    var claimDate by remember { mutableStateOf("") }
    var hospitalId by remember { mutableStateOf("") }
    var claimAmount by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pending") }
    var policyIdError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val canEditDetails = !isEdit || when (appRole) {
        com.example.insuranceapp.data.model.AppRole.CLIENT -> {
            val statusLower = status.lowercase()
            statusLower != "rejected" && statusLower != "denied" && statusLower != "cancelled" && statusLower != "approved" && statusLower != "active"
        }
        else -> appRole.canEditClaimDetails()
    }
    val canEditStatus = appRole.canEditClaimStatus()
    val isClient = appRole == com.example.insuranceapp.data.model.AppRole.CLIENT

    LaunchedEffect(isEdit, isClient) {
        if (!isEdit && isClient && claimDate.isEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            claimDate = sdf.format(Date())
        }
    }

    LaunchedEffect(Unit) {
        policyViewModel.loadPolicies()
    }

    LaunchedEffect(claimId) {
        if (isEdit && claimId != null) {
            viewModel.getClaim(claimId)
        }
    }

    val policiesState = policyViewModel.state

    LaunchedEffect(viewModel.claimDetail, policiesState) {
        if (isEdit && viewModel.claimDetail != null) {
            val claim = viewModel.claimDetail!!
            policyId = claim.policy_id
            claimDate = claim.claim_date
            hospitalId = claim.hospital_id
            claimAmount = claim.claim_amount.toString()
            status = claim.status

            if (policiesState is PolicyState.Success) {
                selectedPolicyName = policiesState.policies.find { it.policy_id == claim.policy_id }
                    ?.let { "${it.policy_type} - ${it.policy_id}" } ?: "Policy: ${claim.policy_id}"
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ClaimViewModel.UiEvent.NavigateBack -> {
                    onBack()
                }
                is ClaimViewModel.UiEvent.ShowSnackbar -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEdit) "Edit Claim" else "Create Claim") },
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
                // Policy Dropdown
                Text("Select Policy", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = canEditDetails && policyDropdownExpanded,
                    onExpandedChange = { if (canEditDetails) policyDropdownExpanded = !policyDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPolicyName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Search & Select Policy") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = policyDropdownExpanded) },
                        isError = policyIdError != null,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = policyDropdownExpanded,
                        onDismissRequest = { policyDropdownExpanded = false }
                    ) {
                        when (policiesState) {
                            is PolicyState.Loading -> {
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Loading policies...")
                                        }
                                    },
                                    onClick = {}
                                )
                            }
                            is PolicyState.Error -> {
                                DropdownMenuItem(
                                    text = { Text("Error: ${(policiesState as PolicyState.Error).message}", color = MaterialTheme.colorScheme.error) },
                                    onClick = { policyViewModel.loadPolicies() }
                                )
                            }
                            is PolicyState.Success -> {
                                val policies = (policiesState as PolicyState.Success).policies
                                if (policies.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No policies found") },
                                        onClick = { policyDropdownExpanded = false }
                                    )
                                } else {
                                    policies.forEach { pol ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(pol.policy_type, fontWeight = FontWeight.Bold)
                                                    Text("ID: ${pol.policy_id}", style = MaterialTheme.typography.bodySmall)
                                                }
                                            },
                                            onClick = {
                                                policyId = pol.policy_id ?: ""
                                                selectedPolicyName = "${pol.policy_type} - ${pol.policy_id}"
                                                policyDropdownExpanded = false
                                                policyIdError = null
                                            },
                                            trailingIcon = {
                                                if (policyId == pol.policy_id) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            else -> {
                                // For Idle state, trigger load if not already loading
                                LaunchedEffect(Unit) {
                                    policyViewModel.loadPolicies()
                                }
                            }
                        }
                    }
                }
                if (policyIdError != null) {
                    Text(
                        text = policyIdError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                AppDatePicker(
                    label = "Claim Date",
                    selectedDate = claimDate,
                    onDateSelected = { claimDate = it },
                    enabled = !isClient
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = hospitalId,
                    onValueChange = { hospitalId = it },
                    label = "Hospital ID",
                    enabled = canEditDetails,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = claimAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) claimAmount = it; amountError = null
                    },
                    label = "Claim Amount (₹)",
                    isError = amountError != null,
                    errorMessage = amountError,
                    enabled = canEditDetails,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Status", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                if (canEditStatus) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Active", "Pending", "Denied").forEach { s ->
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                RadioButton(
                                    selected = status == s,
                                    onClick = { status = s },
                                    enabled = true
                                )
                                Text(s, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    // Read-only status display for roles that cannot edit status
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Current Status") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                if (canEditDetails || canEditStatus) {
                    Button(
                        onClick = {
                            var isValid = true
                            // Only validate detail fields if the user can edit them
                            if (canEditDetails) {
                                if (policyId.isBlank()) {
                                    policyIdError = "Policy ID is required"; isValid = false
                                }
                                val amountInt = claimAmount.toIntOrNull()
                                if (amountInt == null || amountInt <= 0) {
                                    amountError = "Amount must be a positive number"; isValid = false
                                }
                            }

                            if (isValid) {
                                HapticHelper.vibrate(context)
                                // If user is a Client, associate their Cust_id automatically
                            val effectiveCustId = if (userViewModel.currentRole?.lowercase() == "client") {
                                userViewModel.linkedCustomer?.customer_id
                            } else {
                                viewModel.claimDetail?.customer_id // Keep existing if editing
                            }

                            val claim = Claim(
                                Claim_ID = if (isEdit) claimId else null,
                                policy_id = policyId,
                                claim_date = claimDate,
                                hospital_id = hospitalId,
                                claim_amount = claimAmount.toIntOrNull() ?: 0,
                                status = status,
                                customer_id = effectiveCustId,
                                user_id = userViewModel.currentUserId
                            )

                            if (isEdit && claimId != null) {
                                viewModel.updateClaim(claimId, claim)
                            } else {
                                viewModel.createClaim(claim)
                            }
                        }
                    },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEdit) "Update Claim" else "Save Claim")
                    }
                }
            }
        }
    }
}