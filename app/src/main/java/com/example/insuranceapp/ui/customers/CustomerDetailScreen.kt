package com.example.insuranceapp.ui.customers

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
import com.example.insuranceapp.data.model.canEditCustomer
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    custId: String,
    viewModel: CustomerViewModel,
    claimViewModel: com.example.insuranceapp.ui.claims.ClaimViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onClaimClick: (String) -> Unit
) {
    val customer = viewModel.customerDetail
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(custId) {
        viewModel.getCustomer(custId)
        claimViewModel.loadClaims()
    }

    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val headerGradient = if (isDark) PrimaryGradientDark else PrimaryGradientLight

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
                        val appRole = userViewModel.appRole
                        val canEdit = appRole.canEditCustomer()
                        val canDelete = appRole == com.example.insuranceapp.data.model.AppRole.ADMIN

                        if (canEdit) {
                            IconButton(onClick = { onEdit(custId) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        if (canDelete) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
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
            if (customer == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        GradientHeader(
                            title = customer.name,
                            subtitle = "Member since 2023",
                            icon = Icons.Default.Person,
                            brush = headerGradient
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 0.dp)
                            .offset(y = (-20).dp)
                    ) {
                        AppCard {
                            Text(
                                text = "Contact Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Customer ID",
                                value = customer.customer_id ?: "N/A",
                                icon = Icons.Default.Badge
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Email Address",
                                value = customer.email ?: "N/A",
                                icon = Icons.Default.Email
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Phone Number",
                                value = customer.phone.toString(),
                                icon = Icons.Default.Phone
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Address",
                                value = customer.address,
                                icon = Icons.Default.LocationOn
                            )
                        }

                        AppCard {
                            Text(
                                text = "Personal Metadata",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Age Group",
                                value = "${customer.age} years",
                                icon = Icons.Default.Cake
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Assigned Claims",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val claimsState = claimViewModel.state
                        val assignedClaims = when (claimsState) {
                            is com.example.insuranceapp.ui.claims.ClaimState.Success -> {
                                claimsState.claims.filter { it.customer_id == custId }
                            }
                            else -> emptyList()
                        }

                        if (assignedClaims.isEmpty()) {
                            Text(
                                text = "No claims associated with this customer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        } else {
                            for (claim in assignedClaims) {
                                AppCard(
                                    onClick = { claim.Claim_ID?.let { onClaimClick(it) } },
                                    accentBrush = headerGradient
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Claim #${claim.Claim_ID ?: "N/A"}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outlineVariant
                                        )
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
                        viewModel.deleteCustomer(custId)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }

            }
        }
    }
