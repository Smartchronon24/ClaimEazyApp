package com.example.insuranceapp.ui.roles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Role
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.PrimaryGradientDark
import com.example.insuranceapp.ui.theme.PrimaryGradientLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleListScreen(
    viewModel: RoleViewModel,
    onRoleClick: (Int) -> Unit,
    onAddRoleClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.loadRoles()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ModernSearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search roles..."
        )

        when (val state = viewModel.state) {
            is RoleState.Loading -> LoadingScreen()
            is RoleState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.loadRoles() })
            is RoleState.Success -> {
                val filteredRoles = state.roles.filter {
                    it.role_name.contains(searchQuery, ignoreCase = true) ||
                    it.role_id.toString().contains(searchQuery, ignoreCase = true)
                }
                if (filteredRoles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No roles found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredRoles) { role ->
                            RoleItem(
                                role = role,
                                onClick = { onRoleClick(role.role_id) }
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun RoleItem(role: Role, onClick: () -> Unit) {
    AppCard(
        onClick = onClick,
        accentBrush = if (isSystemInDarkTheme()) PrimaryGradientDark else PrimaryGradientLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = role.role_name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "ID: ${role.role_id}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
