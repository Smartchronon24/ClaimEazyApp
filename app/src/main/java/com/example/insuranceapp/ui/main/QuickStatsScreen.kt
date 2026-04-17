package com.example.insuranceapp.ui.main

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.insuranceapp.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import com.example.insuranceapp.data.model.*
import com.example.insuranceapp.ui.components.ErrorScreen
import com.example.insuranceapp.ui.components.LoadingScreen
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickStatsScreen(
    viewModel: QuickStatsViewModel,
    userViewModel: com.example.insuranceapp.ui.users.UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val primaryGradient = if (isDark) PrimaryGradientDark else PrimaryGradientLight
    val view = LocalView.current
    val appRole = userViewModel.appRole
    val isRestricted = appRole == AppRole.CLIENT || appRole == AppRole.APPROVER

    var selectedDetailType by remember { mutableStateOf<StatDetailType?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDetailType) {
        if (selectedDetailType != null) {
            showSheet = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
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
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    onBack()
                                },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                val headerTitle = if (appRole == AppRole.ADMIN || appRole == AppRole.ETL) "Quick Stats" else "Your Info"
                                Text(
                                    text = headerTitle,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 32.sp,
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
                                    text = "Real-time metrics & insights",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Pulse animation for real-time indicator
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green.copy(alpha = alpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Live",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            if (isRestricted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Access Restricted",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (appRole == AppRole.APPROVER) 
                                "This dashboard is reserved for Admin & ETL users."
                            else 
                                "Personal insights and records will appear here as the system collects more data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                when (val state = uiState) {
                    is QuickStatsUiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
                    is QuickStatsUiState.Error -> ErrorScreen(
                        message = state.message,
                        onRetry = { /* viewModel.fetchStats() */ },
                        modifier = Modifier.padding(padding)
                    )
                    is QuickStatsUiState.Success -> {
                        QuickStatsContent(
                            insights = state.insights,
                            modifier = Modifier.padding(padding),
                            onDetailClick = { selectedDetailType = it }
                        )
                    }
                }
            }
        }

        if (showSheet && selectedDetailType != null) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showSheet = false
                    selectedDetailType = null
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                StatDetailContent(
                    type = selectedDetailType!!,
                    insights = (uiState as? QuickStatsUiState.Success)?.insights
                )
            }
        }
    }
}

@Composable
fun QuickStatsContent(
    insights: AdminInsights,
    modifier: Modifier = Modifier,
    onDetailClick: (StatDetailType) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- OVERVIEW CARDS ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val totalRevenue = insights.payments?.total_revenue ?: 0
            val totalClaims = insights.claims?.total ?: 0
            
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Revenue",
                value = "₹${formatCurrency(totalRevenue)}",
                subtitle = "Total collected",
                icon = Icons.Default.Payments,
                gradient = PrimaryGradientDark,
                onClick = { onDetailClick(StatDetailType.PAYMENTS) }
            )
            
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Claims",
                value = totalClaims.toString(),
                subtitle = "Total submitted",
                icon = Icons.Default.Description,
                gradient = WarningGradient,
                onClick = { onDetailClick(StatDetailType.CLAIMS) }
            )
        }

        // --- SECTION: USER & CUSTOMER GROWTH ---
        StatSectionHeader("Network Overview")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Total Users",
                value = (insights.users?.total ?: 0).toString(),
                subtitle = "Across all roles",
                icon = Icons.Default.Group,
                gradient = SuccessGradient,
                onClick = { onDetailClick(StatDetailType.USERS) }
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Customers",
                value = (insights.users?.by_role?.get("CLIENT") ?: 0).toString(),
                subtitle = "Active clients",
                icon = Icons.Default.Person,
                gradient = Brush.linearGradient(colors = listOf(Color(0xFF818CF8), Color(0xFF6366F1))),
                onClick = { onDetailClick(StatDetailType.CUSTOMERS) }
            )
        }

        // --- SECTION: CLAIMS STATUS (MICRO CHART) ---
        insights.claims?.let { claims ->
            DashboardCard(title = "Claims Status", icon = Icons.Default.PieChart) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val approved = claims.by_status?.get("Approved") ?: 0
                    val pending = claims.by_status?.get("Pending") ?: 0
                    val rejected = claims.by_status?.get("Rejected") ?: 0
                    val total = claims.total.takeIf { it > 0 } ?: 1

                    StatusBar("Approved", approved, total, SuccessGreen)
                    StatusBar("Pending", pending, total, PendingAmber)
                    StatusBar("Rejected", rejected, total, RejectedRed)
                }
            }
        }

        // --- SECTION: POLICIES ---
        insights.policies?.let { policies ->
            DashboardCard(title = "Policy Status", icon = Icons.Default.VerifiedUser) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CircularStat("Active", policies.active, SuccessGreen)
                    CircularStat("Expired", policies.expired, RejectedRed)
                    CircularStat("Upcoming", policies.upcoming, Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val view = LocalView.current
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onClick()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                
                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun StatusBar(label: String, value: Int, total: Int, color: Color) {
    val progress = value.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "progress")
    
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$value", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun CircularStat(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(60.dp),
                color = color.copy(alpha = 0.1f),
                strokeWidth = 6.dp
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun StatDetailContent(type: StatDetailType, insights: AdminInsights?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .heightIn(min = 300.dp, max = 600.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = when(type) {
                StatDetailType.CLAIMS -> "Claims Performance"
                StatDetailType.CUSTOMERS -> "Customer Demographics"
                StatDetailType.PAYMENTS -> "Financial Insights"
                StatDetailType.USERS -> "User Roles Distribution"
                StatDetailType.POLICIES -> "Policy Breakdown"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        when (type) {
            StatDetailType.CLAIMS -> {
                insights?.claims?.let { claims ->
                    DetailItem("Total Claimed Amount", "₹${formatCurrency(claims.totalclaimed_amt)}")
                    DetailItem("Average Claim Size", "₹${if(claims.total > 0) formatCurrency(claims.totalclaimed_amt / claims.total) else "0"}")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Status Distribution", fontWeight = FontWeight.Bold)
                    claims.by_status?.forEach { (status, count) ->
                        StatusBar(status, count, claims.total, when(status) {
                            "Approved" -> SuccessGreen
                            "Pending" -> PendingAmber
                            else -> RejectedRed
                        })
                    }
                }
            }
            StatDetailType.CUSTOMERS -> {
                insights?.customers?.let { customers ->
                    val clientCount = insights.users?.by_role?.get("CLIENT") ?: 0
                    DetailItem("Total Active Customers (Clients)", clientCount.toString())
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text("Age Distribution", fontWeight = FontWeight.Bold)
                    val totalAge = customers.age_group?.values?.sum() ?: 1
                    customers.age_group?.entries?.sortedBy { it.key }?.forEach { (group, count) ->
                        StatusBar(group, count, totalAge, MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Top Locations", fontWeight = FontWeight.Bold)
                    val totalLoc = customers.address?.values?.sum() ?: 1
                    customers.address?.entries?.sortedByDescending { it.value }?.take(10)?.forEach { (loc, count) ->
                        StatusBar(loc, count, totalLoc, MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            StatDetailType.PAYMENTS -> {
                insights?.payments?.let { payments ->
                    DetailItem("Pending Revenue", "₹${formatCurrency(payments.pending)}", PendingAmber)
                    DetailItem("Total Revenue", "₹${formatCurrency(payments.total_revenue)}", SuccessGreen)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Payment Methods", fontWeight = FontWeight.Bold)
                    val totalPay = payments.payment_mode?.values?.sum() ?: 1
                    payments.payment_mode?.forEach { (mode, count) ->
                        StatusBar(mode, count, totalPay, MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
            StatDetailType.USERS -> {
                insights?.users?.let { users ->
                    Text("Role Breakdown", fontWeight = FontWeight.Bold)
                    users.by_role?.forEach { (role, count) ->
                        StatusBar(role, count, users.total, MaterialTheme.colorScheme.primary)
                    }
                }
            }
            else -> {
                Text("More details coming soon...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Extra space for sheet padding
    }
}

@Composable
fun DetailItem(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

enum class StatDetailType {
    CLAIMS, CUSTOMERS, PAYMENTS, USERS, POLICIES
}

fun formatCurrency(amount: Long): String {
    return String.format("%,d", amount)
}
