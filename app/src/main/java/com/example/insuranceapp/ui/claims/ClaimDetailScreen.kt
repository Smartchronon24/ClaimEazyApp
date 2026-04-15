package com.example.insuranceapp.ui.claims

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.canEditClaimDetails
import com.example.insuranceapp.data.model.canEditClaimStatus
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailScreen(
    claimId: String,
    viewModel: ClaimViewModel,
    customerViewModel: com.example.insuranceapp.ui.customers.CustomerViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val claim = viewModel.claimDetail
    val appRole = userViewModel.appRole
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf(false) }

    LaunchedEffect(claimId) {
        viewModel.getClaim(claimId)
    }

    LaunchedEffect(claim?.customer_id) {
        claim?.customer_id?.let { custId ->
            if (custId != "None" && custId.isNotBlank()) {
                customerViewModel.getCustomer(custId)
            }
        }
    }

    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    modifier = Modifier.offset(y = (-20).dp),
                    actions = {
                        val canDelete = when (appRole) {
                            com.example.insuranceapp.data.model.AppRole.ADMIN -> true
                            com.example.insuranceapp.data.model.AppRole.CLIENT -> {
                                val isOwner = claim?.customer_id != null && claim.customer_id == userViewModel.currentCustId
                                val isPending = claim?.status?.lowercase() == "pending"
                                isOwner && isPending
                            }
                            else -> false
                        }
                        // For CLIENT: can only edit claims they own AND are not rejected/denied
                        val canEdit = when (appRole) {
                            com.example.insuranceapp.data.model.AppRole.CLIENT -> {
                                val isOwner = claim?.customer_id != null && claim.customer_id == userViewModel.currentCustId
                                val isEditableStatus = claim?.status?.lowercase()?.let { 
                                    it != "rejected" && it != "denied" && it != "cancelled" && it != "approved" && it != "active"
                                } ?: true
                                isOwner && isEditableStatus
                            }
                            else -> appRole.canEditClaimDetails() || appRole.canEditClaimStatus()
                        }
                        
                        if (canEdit) {
                            IconButton(onClick = { onEdit(claimId) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        if (canDelete) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (claim == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                val statusBrush = when (claim.status.lowercase()) {
                    "active", "approved" -> SuccessGradient
                    "pending" -> WarningGradient
                    "denied", "rejected", "cancelled" -> DangerGradient
                    else -> if (isDark) PrimaryGradientDark else PrimaryGradientLight
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    GradientHeader(
                        title = "Claim #${claim.Claim_ID}",
                        subtitle = "Status: ${claim.status}",
                        icon = Icons.Default.Description,
                        brush = statusBrush
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 0.dp)
                            .offset(y = (-20).dp)
                    ) {
                        AppCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Claim Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusChip(status = claim.status)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(label = "Policy Ref", value = claim.policy_id, icon = Icons.Default.Receipt)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                            InfoRow(label = "Settlement Amount", value = "₹${claim.claim_amount}", icon = Icons.Default.Payments)
                        }

                        AppCard {
                            Text(
                                text = "Verification Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(label = "Hospital ID", value = claim.hospital_id, icon = Icons.Default.LocalHospital)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))
                            InfoRow(label = "Date of Filing", value = claim.claim_date, icon = Icons.Default.Event)
                        }

                        AppCard {
                            Text(
                                text = "Ownership Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val isAssigned = !claim.customer_id.isNullOrBlank() && claim.customer_id != "None"
                            
                            if (isAssigned) {
                                val assignedLabel = if (customerViewModel.customerDetail?.customer_id == claim.customer_id) {
                                    customerViewModel.customerDetail?.name ?: "Cust #${claim.customer_id}"
                                } else {
                                    "Cust #${claim.customer_id}"
                                }
                                InfoRow(label = "Assigned To", value = assignedLabel, icon = Icons.Default.Person)
                                if (appRole == com.example.insuranceapp.data.model.AppRole.ADMIN) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.deassignClaim(claimId) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                                    ) {
                                        Icon(Icons.Default.LinkOff, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Deassign Claim")
                                    }
                                }
                            } else {
                                Text("This claim is currently unassigned.", style = MaterialTheme.typography.bodyMedium)
                                if (appRole == com.example.insuranceapp.data.model.AppRole.ADMIN) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { 
                                            customerViewModel.loadCustomers()
                                            showAssignDialog = true 
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.AddLink, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Assign to Customer")
                                    }
                                }
                            }
                        }


                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                onConfirm = {
                    viewModel.deleteClaim(claimId)
                    showDeleteDialog = false
                    onBack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (showAssignDialog) {
            val customerState = customerViewModel.state
            AlertDialog(
                onDismissRequest = { showAssignDialog = false },
                title = { Text("Select Customer to Assign") },
                text = {
                    Column {
                        Text("Search and pick a customer for this claim.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        when (customerState) {
                            is com.example.insuranceapp.ui.customers.CustomerState.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is com.example.insuranceapp.ui.customers.CustomerState.Success -> {
                                val customers = customerState.customers
                                Column(
                                    modifier = Modifier
                                        .heightIn(max = 300.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (customer in customers) {
                                        AppCard(
                                            onClick = {
                                                viewModel.assignClaim(claimId, customer.customer_id!!) {
                                                    showAssignDialog = false
                                                }
                                            }
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(customer.name, fontWeight = FontWeight.Bold)
                                                    Text("ID: ${customer.customer_id}", style = MaterialTheme.typography.bodySmall)
                                                }
                                                Icon(Icons.Default.AddLink, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                            }
                            is com.example.insuranceapp.ui.customers.CustomerState.Error -> {
                                Text("Error: ${customerState.message}", color = MaterialTheme.colorScheme.error)
                            }
                            else -> {}
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAssignDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
}
