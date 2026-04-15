package com.example.insuranceapp.ui.main

import androidx.compose.foundation.background
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import com.example.insuranceapp.utils.HapticHelper
import android.view.HapticFeedbackConstants
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuranceapp.ui.theme.*
import com.example.insuranceapp.R

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDark: Boolean,
    onExploreClick: () -> Unit
) {
    val welcomeMessage by viewModel.welcomeMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val syncedKey by viewModel.connectionKey.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current
    
    var isRevealing by remember { mutableStateOf(false) }
    var buttonCenter by remember { mutableStateOf(Offset.Zero) }
    var showConnectionDialog by remember { mutableStateOf(false) }
    var ngrokKey by remember { mutableStateOf(syncedKey ?: "") }

    // Sync local state when remote key updates
    androidx.compose.runtime.LaunchedEffect(syncedKey) {
        if (syncedKey != null) {
            ngrokKey = syncedKey!!
        }
    }

    val revealRadius by animateFloatAsState(
        targetValue = if (isRevealing) 3000f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isRevealing) {
                onExploreClick()
                isRevealing = false
            }
        },
        label = "reveal_radius"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "logo_transition")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_offset"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_rotation"
    )

    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val primaryGradient = if (isDark) PrimaryGradientDark else PrimaryGradientLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Decorative Top Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(primaryGradient, RoundedCornerShape(bottomStart = 80.dp))
                .padding(15.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .offset(y = offsetY.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iz_logo),
                    contentDescription = "Insurance Manager Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp), // To avoid being hidden by the top box if it were smaller
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(200.dp))

            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    text = welcomeMessage,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        lineHeight = 40.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your all-in-one solution for managing insurance customers and claims with ease.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 50.dp)
            )
        }

        // Bottom Action Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { 
                    HapticHelper.vibrate(context)
                    isRevealing = true 
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
                    .onGloballyPositioned { coordinates ->
                        buttonCenter = coordinates.boundsInRoot().center
                    },
                enabled = isConnected,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Manage your profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (!isConnected) {
                Button(
                    onClick = { showConnectionDialog = true },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.CloudSync, 
                        contentDescription = null, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect to Cloud!", fontWeight = FontWeight.SemiBold)
                }
            } else {
                TextButton(
                    onClick = { showConnectionDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        Icons.Default.CloudSync, 
                        contentDescription = null, 
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Change Connection Settings",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        if (revealRadius > 0f) {
            val revealColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = revealColor,
                    radius = revealRadius,
                    center = buttonCenter
                )
            }
        }

        if (showConnectionDialog) {
            AlertDialog(
                onDismissRequest = { showConnectionDialog = false },
                title = { Text("Update Key") },
                text = {
                    Column {
                        Text(
                            "Enter the new NGROK key to connect with the server.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = ngrokKey,
                            onValueChange = { ngrokKey = it },
                            label = { Text("NGROK Key (e.g. 1c91-1c2...)") },
                            placeholder = { Text("Enter key here") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Template: https://[key].ngrok-free.app",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateConnectionKey(ngrokKey)
                            showConnectionDialog = false
                        }
                    ) {
                        Text("Connect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConnectionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
