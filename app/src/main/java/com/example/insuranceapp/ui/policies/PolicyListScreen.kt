package com.example.insuranceapp.ui.policies

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Policy
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.PrimaryGradientDark
import com.example.insuranceapp.ui.theme.PrimaryGradientLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyListScreen(
    viewModel: PolicyViewModel,
    onPolicyClick: (String) -> Unit,
    onAddPolicyClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.loadPolicies()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ModernSearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search policies..."
        )

        when (val state = viewModel.state) {
            is PolicyState.Loading -> LoadingScreen()
            is PolicyState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.loadPolicies() })
            is PolicyState.Success -> {
                val filteredPolicies = state.policies.filter {
                    it.policy_type.contains(searchQuery, ignoreCase = true) ||
                    it.policy_id?.contains(searchQuery, ignoreCase = true) == true
                }
                if (filteredPolicies.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Policy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No policies yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredPolicies) { policy ->
                            PolicyItem(
                                policy = policy,
                                onClick = { policy.policy_id?.let { onPolicyClick(it) } }
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
fun PolicyItem(policy: Policy, onClick: () -> Unit) {
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
                modifier = Modifier.size(52.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Policy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = policy.policy_type,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Premium: ₹${policy.premium} • Coverage: ₹${policy.coverage_amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "#${policy.policy_id}",
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
