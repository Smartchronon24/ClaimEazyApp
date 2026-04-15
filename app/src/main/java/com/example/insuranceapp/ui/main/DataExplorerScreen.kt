package com.example.insuranceapp.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.insuranceapp.R
import com.example.insuranceapp.ui.claims.ClaimListScreen
import com.example.insuranceapp.ui.claims.ClaimViewModel
import com.example.insuranceapp.ui.customers.CustomerListScreen
import com.example.insuranceapp.ui.customers.CustomerViewModel
import com.example.insuranceapp.data.model.canCreateClaim
import com.example.insuranceapp.data.model.canCreateAnythingExceptClaim
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExplorerScreen(
    customerViewModel: CustomerViewModel,
    claimViewModel: ClaimViewModel,
    policyViewModel: com.example.insuranceapp.ui.policies.PolicyViewModel,
    paymentViewModel: com.example.insuranceapp.ui.payments.PaymentViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    roleViewModel: com.example.insuranceapp.ui.roles.RoleViewModel,
    onCustomerClick: (String) -> Unit,
    onAddCustomerClick: () -> Unit,
    onClaimClick: (String) -> Unit,
    onAddClaimClick: () -> Unit,
    onPolicyClick: (String) -> Unit,
    onAddPolicyClick: () -> Unit,
    onPaymentClick: (String) -> Unit,
    onAddPaymentClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onAddUserClick: () -> Unit,
    onRoleClick: (Int) -> Unit,
    onAddRoleClick: () -> Unit,
    onBack: () -> Unit,
    isDark: Boolean
) {
    val appRole = userViewModel.appRole
    val dataOptions = when (appRole) {
        com.example.insuranceapp.data.model.AppRole.APPROVER ->
            listOf("Customers", "Claims", "Policies", "Payments")
        com.example.insuranceapp.data.model.AppRole.CLIENT ->
            listOf("Claims", "Policies")
        else ->
            listOf("Customers", "Claims", "Policies", "Payments", "Users", "Roles")
    }
    val initialOption = remember(dataOptions) {
        val persisted = userViewModel.selectedExplorerOption
        if (persisted != null && persisted in dataOptions) persisted else dataOptions[0]
    }
    var selectedOption by remember { mutableStateOf(initialOption) }

    LaunchedEffect(selectedOption) {
        userViewModel.selectedExplorerOption = selectedOption
    }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val primaryGradient = if (isDark) PrimaryGradientDark else PrimaryGradientLight

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bannerartiz),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Gradient overlay to ensure text legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Data Explorer",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 32.sp, // Increased size as requested
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = Color.Black.copy(alpha = 0.4f),
                                            offset = Offset(2f, 2f),
                                            blurRadius = 6f
                                        )
                                    ),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Browse & manage your records",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                val appRole = userViewModel.appRole
                val canCreate = when (selectedOption) {
                    "Claims" -> appRole.canCreateClaim()
                    "Customers" -> appRole.canCreateAnythingExceptClaim()
                    "Policies" -> appRole.canCreateAnythingExceptClaim()
                    "Payments" -> appRole.canCreateAnythingExceptClaim()
                    "Users" -> appRole.canCreateAnythingExceptClaim()
                    "Roles" -> appRole.canCreateAnythingExceptClaim()
                    else -> false
                }
                
                if (canCreate) {
                    FloatingActionButton(
                        onClick = {
                            when (selectedOption) {
                                "Customers" -> onAddCustomerClick()
                                "Claims" -> onAddClaimClick()
                                "Policies" -> onAddPolicyClick()
                                "Payments" -> onAddPaymentClick()
                                "Users" -> onAddUserClick()
                                "Roles" -> onAddRoleClick()
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        shape = CircleShape,
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(AccentGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Dropdown Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp) // Removed bottom padding to bring search bar closer
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOption,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Data Table") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (selectedOption) {
                                        "Customers" -> Icons.Default.People
                                        "Claims" -> Icons.Default.Description
                                        "Policies" -> Icons.Default.Policy
                                        "Payments" -> Icons.Default.Payment
                                        "Users" -> Icons.Default.Person
                                        "Roles" -> Icons.Default.AdminPanelSettings
                                        else -> Icons.Default.TableChart
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            dataOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = when (option) {
                                                    "Customers" -> Icons.Default.People
                                                    "Claims" -> Icons.Default.Description
                                                    "Policies" -> Icons.Default.Policy
                                                    "Payments" -> Icons.Default.Payment
                                                    "Users" -> Icons.Default.Person
                                                    "Roles" -> Icons.Default.AdminPanelSettings
                                                    else -> Icons.Default.TableChart
                                                },
                                                contentDescription = null,
                                                tint = if (option == selectedOption)
                                                    MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = option,
                                                fontWeight = if (option == selectedOption)
                                                    FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedOption = option
                                        dropdownExpanded = false
                                    },
                                    leadingIcon = null,
                                    trailingIcon = {
                                        if (option == selectedOption) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Content based on selection
                AnimatedContent(
                    targetState = selectedOption,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    label = "data_table_transition"
                ) { option ->
                    when (option) {
                        "Customers" -> CustomerListScreen(
                            viewModel = customerViewModel,
                            onCustomerClick = onCustomerClick,
                            onAddCustomerClick = onAddCustomerClick
                        )
                        "Claims" -> ClaimListScreen(
                            viewModel = claimViewModel,
                            onClaimClick = onClaimClick,
                            onAddClaimClick = onAddClaimClick,
                            filterCustId = if (appRole == com.example.insuranceapp.data.model.AppRole.CLIENT)
                                userViewModel.currentCustId else null
                        )
                        "Policies" -> com.example.insuranceapp.ui.policies.PolicyListScreen(
                            viewModel = policyViewModel,
                            onPolicyClick = onPolicyClick,
                            onAddPolicyClick = onAddPolicyClick
                        )
                        "Payments" -> com.example.insuranceapp.ui.payments.PaymentListScreen(
                            viewModel = paymentViewModel,
                            onPaymentClick = onPaymentClick,
                            onAddPaymentClick = onAddPaymentClick
                        )
                        "Users" -> com.example.insuranceapp.ui.users.UserListScreen(
                            viewModel = userViewModel,
                            onUserClick = onUserClick,
                            onAddUserClick = onAddUserClick
                        )
                        "Roles" -> com.example.insuranceapp.ui.roles.RoleListScreen(
                            viewModel = roleViewModel,
                            onRoleClick = onRoleClick,
                            onAddRoleClick = onAddRoleClick
                        )
                    }
                }
            }
        }
    }
}
