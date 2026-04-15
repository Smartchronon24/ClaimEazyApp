package com.example.insuranceapp.ui.claims

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Claim
import com.example.insuranceapp.ui.components.*
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimListScreen(
    viewModel: ClaimViewModel,
    onClaimClick: (String) -> Unit,
    onAddClaimClick: () -> Unit,
    filterCustId: String? = null  // If set, only show claims for this customer
) {
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.loadClaims()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ModernSearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search claims..."
        )

        when (val state = viewModel.state) {
            is ClaimState.Loading -> LoadingScreen()
            is ClaimState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.loadClaims() })
            is ClaimState.Success -> {
                // First filter by ownership (if CLIENT role)
                val ownedClaims = if (filterCustId != null) {
                    state.claims.filter { it.customer_id == filterCustId }
                } else {
                    state.claims
                }
                val filteredClaims = ownedClaims.filter {
                    it.Claim_ID.toString().contains(searchQuery, ignoreCase = true) ||
                    it.policy_id.contains(searchQuery, ignoreCase = true) ||
                    it.status.contains(searchQuery, ignoreCase = true)
                }
                if (filteredClaims.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No claims yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredClaims) { claim ->
                            ClaimItem(
                                claim = claim,
                                onClick = { onClaimClick(claim.Claim_ID.toString()) }
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
fun ClaimItem(claim: Claim, onClick: () -> Unit) {
    val statusBrush = when (claim.status.lowercase()) {
        "active", "approved" -> SuccessGradient
        "pending" -> WarningGradient
        "denied", "rejected", "cancelled" -> DangerGradient
        else -> PrimaryGradientDark
    }

    AppCard(
        onClick = onClick,
        accentBrush = statusBrush
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Claim #${claim.Claim_ID}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Policy: ${claim.policy_id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = claim.claim_date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            // Status Badge & Arrow
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(status = claim.status)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}
