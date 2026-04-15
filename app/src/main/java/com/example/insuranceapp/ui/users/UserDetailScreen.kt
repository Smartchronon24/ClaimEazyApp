package com.example.insuranceapp.ui.users

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
fun UserDetailScreen(
    userId: String,
    viewModel: UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val user = viewModel.userDetail
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.getUser(userId)
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
                        val appRole = viewModel.appRole
                        val canEditOrDelete = appRole == com.example.insuranceapp.data.model.AppRole.ADMIN
                        
                        if (canEditOrDelete) {
                            IconButton(onClick = { onEdit(userId) }) {
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
            if (user == null) {
                LoadingScreen(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        GradientHeader(
                            title = user.username,
                            subtitle = "User ID: #${user.user_id}",
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
                                text = "Account Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Username",
                                value = user.username,
                                icon = Icons.Default.AccountCircle
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Password",
                                value = "********", // Conceal for security
                                icon = Icons.Default.Lock
                            )
                        }

                        AppCard {
                            Text(
                                text = "Permissions & Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(
                                label = "Assigned Role ID",
                                value = user.role_id.toString(),
                                icon = Icons.Default.Badge
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.3f
                                ), modifier = Modifier.padding(vertical = 12.dp)
                            )
                            InfoRow(
                                label = "Status",
                                value = user.status,
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
                        viewModel.deleteUser(userId)
                        showDeleteDialog = false
                        onBack()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}
