package com.example.insuranceapp.ui.policies

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
fun PolicyDetailScreen(
    policyId: String,
    viewModel: PolicyViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val policy = viewModel.policyDetail
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(policyId) {
        viewModel.getPolicy(policyId)
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
                            IconButton(onClick = { onEdit(policyId) }) {
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
            if (policy == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        GradientHeader(
                            title = policy.policy_type,
                            subtitle = "Policy #${policy.policy_id}",
                            icon = Icons.Default.Policy,
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
                                text = "Policy Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Policy ID",
                                value = policy.policy_id ?: "N/A",
                                icon = Icons.Default.Badge
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Type",
                                value = policy.policy_type,
                                icon = Icons.Default.Category
                            )
                        }

                        AppCard {
                            Text(
                                text = "Coverage & Premium",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Premium Amount",
                                value = "$${policy.premium}",
                                icon = Icons.Default.AttachMoney
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Coverage Amount",
                                value = "$${policy.coverage_amount}",
                                icon = Icons.Default.HealthAndSafety
                            )
                        }

                        AppCard {
                            Text(
                                text = "Timeline",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Start Date",
                                value = policy.start_date,
                                icon = Icons.Default.DateRange
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "End Date",
                                value = policy.end_date,
                                icon = Icons.Default.EventBusy
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            if (showDeleteDialog) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        viewModel.deletePolicy(policyId)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}
