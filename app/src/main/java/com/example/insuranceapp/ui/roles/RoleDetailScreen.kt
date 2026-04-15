package com.example.insuranceapp.ui.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDetailScreen(
    roleId: Int,
    viewModel: RoleViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val role = viewModel.roleDetail
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roleId) {
        viewModel.getRole(roleId)
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
                            IconButton(onClick = { onEdit(roleId) }) {
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
            if (role == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        GradientHeader(
                            title = role.role_name,
                            subtitle = "Role ID: #${role.role_id}",
                            icon = Icons.Default.AdminPanelSettings,
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
                                text = "Role Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Role Name",
                                value = role.role_name,
                                icon = Icons.Default.Label
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Unique Identifier",
                                value = role.role_id.toString(),
                                icon = Icons.Default.Fingerprint
                            )
                        }

                        AppCard {
                            Text(
                                text = "Description & Permissions",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = role.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            if (showDeleteDialog) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        viewModel.deleteRole(roleId)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}
