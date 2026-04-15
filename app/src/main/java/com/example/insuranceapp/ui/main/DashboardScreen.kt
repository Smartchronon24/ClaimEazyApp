package com.example.insuranceapp.ui.main

import com.example.insuranceapp.ui.users.UserAccountViewModel

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.insuranceapp.R
import com.example.insuranceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: UserAccountViewModel,
    isDark: Boolean,
    onViewDetailsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onQuickStatsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    val isLoggedIn = viewModel.isLoggedIn
    val isGuest = viewModel.isGuest
    val currentRole = viewModel.currentRole ?: "Guest"
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val primaryGradient = if (isDark) PrimaryGradientDark else PrimaryGradientLight

    var revealingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var actionCenter by remember { mutableStateOf(Offset.Zero) }
    var actionBrush by remember { mutableStateOf<Brush?>(null) }

    val revealRadius by animateFloatAsState(
        targetValue = if (revealingAction != null) 3000f else 0f,
        animationSpec = tween(durationMillis = 525, easing = FastOutSlowInEasing),
        finishedListener = {
            revealingAction?.invoke()
            revealingAction = null
        },
        label = "reveal_radius"
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Fixed height to prevent "fullscreen" header
                        .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bannerartiz),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(), // Fill only the 180.dp box, not more
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
                            .padding(horizontal = 24.dp)
                            .padding(top = 40.dp, bottom = 24.dp), // Increased top padding for better visual balance below status bar
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Dashboard",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        offset = Offset(2f, 2f),
                                        blurRadius = 6f
                                    )
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            // Glassmorphism style for the welcome tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isLoggedIn) "Welcome, $currentRole" else "Guest Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        
                        if (isLoggedIn) {
                            IconButton(
                                onClick = onAccountClick,
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Account Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // --- GUEST WELCOME SECTION (TOP) ---
                if (isGuest) {
                    val signInGradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                    )
                    DashboardActionCard(
                        title = "Sign In",
                        subtitle = "View all policies and benefits!",
                        icon = Icons.Default.Login,
                        gradient = signInGradient,
                        onClick = { center -> 
                            actionCenter = center
                            actionBrush = signInGradient
                            revealingAction = onSignInClick
                        },
                        isPrimary = true
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Guest Settings Aligned Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        val settingsGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF78909C), Color(0xFF546E7A))
                        )
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Settings",
                            subtitle = "App preferences",
                            icon = Icons.Default.Settings,
                            gradient = settingsGradient,
                            onClick = { center -> 
                                actionCenter = center
                                actionBrush = settingsGradient
                                revealingAction = onSettingsClick 
                            },
                            isPrimary = false
                        )
                    }
                }

                // --- REGISTERED USER SECTION ---
                if (!isGuest) {
                    val viewDetailsGradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF818CF8), Color(0xFF4DD0E1))
                    )
                    DashboardActionCard(
                        title = "View More Details",
                        subtitle = "Browse customers, claims, and all your data tables",
                        icon = Icons.Default.TableChart,
                        gradient = viewDetailsGradient,
                        onClick = { center -> 
                            actionCenter = center
                            actionBrush = viewDetailsGradient
                            revealingAction = onViewDetailsClick 
                        },
                        isPrimary = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Quick Stats",
                            subtitle = "Overview at a glance",
                            icon = Icons.Default.BarChart,
                            gradient = SuccessGradient,
                            onClick = { center -> 
                                actionCenter = center
                                actionBrush = SuccessGradient
                                revealingAction = onQuickStatsClick 
                            },
                            isPrimary = false
                        )
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Reports",
                            subtitle = "Generate insights",
                            icon = Icons.Default.Assessment,
                            gradient = WarningGradient,
                            onClick = { center -> 
                                actionCenter = center
                                actionBrush = WarningGradient
                                revealingAction = onReportsClick 
                            },
                            isPrimary = false
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Notifications",
                            subtitle = "Stay updated",
                            icon = Icons.Default.Notifications,
                            gradient = DangerGradient,
                            onClick = { center -> 
                                actionCenter = center
                                actionBrush = DangerGradient
                                revealingAction = onNotificationsClick 
                            },
                            isPrimary = false
                        )
                        val settingsGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF78909C), Color(0xFF546E7A))
                        )
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Settings",
                            subtitle = "App preferences",
                            icon = Icons.Default.Settings,
                            gradient = settingsGradient,
                            onClick = { center -> 
                                actionCenter = center
                                actionBrush = settingsGradient
                                revealingAction = onSettingsClick 
                            },
                            isPrimary = false
                        )
                    }
                }
            }
        }

        if (revealRadius > 0f) {
            val defaultBrush = SolidColor(MaterialTheme.colorScheme.primary)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = actionBrush ?: defaultBrush,
                    radius = revealRadius,
                    center = actionCenter
                )
            }
        }
    }
}


@Composable
fun DashboardActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: (Offset) -> Unit,
    isPrimary: Boolean
) {
    val view = LocalView.current
    var cardCenter by remember { mutableStateOf(Offset.Zero) }

    Card(
        onClick = { 
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onClick(cardCenter) 
        },
        modifier = modifier
            .then(
                if (isPrimary) Modifier.fillMaxWidth().height(140.dp)
                else Modifier.height(150.dp)
            )
            .onGloballyPositioned { coordinates ->
                cardCenter = coordinates.boundsInRoot().center
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = if (isPrimary) Arrangement.Center else Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isPrimary) 48.dp else 40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(if (isPrimary) 26.dp else 22.dp)
                    )
                }

                if (isPrimary) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = if (isPrimary) MaterialTheme.typography.titleLarge
                               else MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = if (isPrimary) MaterialTheme.typography.bodyMedium
                               else MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2
                    )
                }
            }

            if (isPrimary) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                )
            }
        }
    }
}
