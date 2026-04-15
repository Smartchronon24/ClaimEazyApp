package com.example.insuranceapp.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuranceapp.ui.theme.*
import com.example.insuranceapp.ui.components.AppTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.insuranceapp.utils.HapticHelper
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    viewModel: UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = viewModel.userDetail
    val role = viewModel.currentRole ?: "User"
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val surfaceColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.6f)
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshCurrentUserData()
        
        viewModel.eventFlow.collectLatest { event ->
            if (event is UserAccountViewModel.UiEvent.NavigateBack && !viewModel.isLoggedIn) {
                // This means the user deleted themselves
                onLogout()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = user?.username ?: "Standard User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = role.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // User Info Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = surfaceColor,
                    tonalElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        InfoRow(label = "User ID", value = viewModel.currentUserId ?: "N/A", icon = Icons.Default.Fingerprint)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                        InfoRow(label = "Account Status", value = user?.status ?: "ACTIVE", icon = Icons.Default.VerifiedUser)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                        InfoRow(label = "Current Role", value = role, icon = Icons.Default.Shield)
                    }
                }

                val linkedCustomer = viewModel.linkedCustomer
                if (linkedCustomer != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Linked Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = surfaceColor,
                        tonalElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            InfoRow(label = "Customer ID", value = linkedCustomer.customer_id ?: "N/A", icon = Icons.Default.Badge)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow(label = "Full Name", value = linkedCustomer.name, icon = Icons.Default.PersonOutline)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow(label = "Age", value = linkedCustomer.age.toString(), icon = Icons.Default.CalendarToday)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow(label = "Contact Number", value = linkedCustomer.phone.toString(), icon = Icons.Default.Phone)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow(label = "Email Address", value = linkedCustomer.email ?: "N/A", icon = Icons.Default.Email)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                            InfoRow(label = "Address", value = linkedCustomer.address, icon = Icons.Default.Home)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete Account Button (Destructive)
                TextButton(
                    onClick = { 
                        HapticHelper.vibrate(context)
                        showDeleteDialog = true 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permanently Delete Account", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    passwordInput = ""
                    passwordError = null
                },
                title = { Text("Confirm Account Deletion") },
                text = {
                    Column {
                        Text(
                            "This action is permanent and cannot be undone. All your data will be cleared.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AppTextField(
                            value = passwordInput,
                            onValueChange = { 
                                passwordInput = it
                                passwordError = null
                            },
                            label = "Current Password",
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordError != null,
                            errorMessage = passwordError
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            HapticHelper.vibrate(context)
                            if (user != null && passwordInput == user.password) {
                                viewModel.deleteUser(user.user_id!!)
                                showDeleteDialog = false
                            } else {
                                passwordError = "Incorrect password"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Everything")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeleteDialog = false
                        passwordInput = ""
                        passwordError = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}
