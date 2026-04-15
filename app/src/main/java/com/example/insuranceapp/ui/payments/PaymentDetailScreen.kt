package com.example.insuranceapp.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    paymentId: String,
    viewModel: PaymentViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val payment = viewModel.paymentDetail
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(paymentId) {
        viewModel.getPayment(paymentId)
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
                        val canEditOrDelete = appRole == com.example.insuranceapp.data.model.AppRole.ADMIN
                        
                        if (canEditOrDelete) {
                            IconButton(onClick = { onEdit(paymentId) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
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
            if (payment == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        GradientHeader(
                            title = "₹${payment.payment_amount}",
                            subtitle = "Payment #${payment.payment_id}",
                            icon = Icons.Default.Payment,
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
                                text = "Payment Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Payment ID",
                                value = payment.payment_id ?: "N/A",
                                icon = Icons.Default.Badge
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Policy ID",
                                value = payment.policy_id,
                                icon = Icons.Default.Policy
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Amount",
                                value = "₹${payment.payment_amount}",
                                icon = Icons.Default.AttachMoney
                            )
                        }

                        AppCard {
                            Text(
                                text = "Transaction Info",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Date",
                                value = payment.payment_date,
                                icon = Icons.Default.DateRange
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Mode",
                                value = payment.payment_mode,
                                icon = Icons.Default.CreditCard
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Status",
                                value = payment.payment_status,
                                icon = Icons.Default.Info
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            if (showDeleteDialog) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        viewModel.deletePayment(paymentId)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}
