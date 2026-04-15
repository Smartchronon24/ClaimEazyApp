package com.example.insuranceapp.ui.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuranceapp.R
import com.example.insuranceapp.data.model.UserAccount
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.components.AppDropdown
import com.example.insuranceapp.data.model.LocationConstants
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import com.example.insuranceapp.ui.theme.SuccessGreen
import com.example.insuranceapp.utils.NotificationHelper
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: UserAccountViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var successUserId by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserAccountViewModel.UiEvent.SignupSuccess -> {
                    successUserId = event.userId
                    showSuccessDialog = true
                    // Show system notification
                    NotificationHelper.showSignupNotification(context, event.userId)
                }
                is UserAccountViewModel.UiEvent.NavigateBack -> {
                    onBack()
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Create Account") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.incepteztext),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
                
                Text(
                    text = "Join Us",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                    //modifier = Modifier.padding(top = 16.dp)
                )
                
                Text(
                    text = "Create an account to start managing your insurance.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                AppTextField(
                    value = username,
                    onValueChange = { username = it; usernameError = null },
                    label = "Username",
                    isError = usernameError != null,
                    errorMessage = usernameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = null },
                    label = "Confirm Password",
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    errorMessage = confirmPasswordError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                Text(
                    "Personal Information",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )

                AppTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = "Full Name",
                    isError = nameError != null,
                    errorMessage = nameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = "Email Address",
                    isError = emailError != null,
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = null },
                    label = "Phone Number",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(
                        modifier = Modifier.weight(1f),
                        value = age,
                        onValueChange = { age = it; ageError = null },
                        label = "Age",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        isError = ageError != null,
                        errorMessage = ageError,
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                    AppDropdown(
                        modifier = Modifier.weight(2f),
                        label = "Address (State)",
                        options = LocationConstants.INDIAN_STATES,
                        selectedOption = if (address.isBlank()) "Select State" else address,
                        onOptionSelected = { address = it; addressError = null },
                        isError = addressError != null,
                        errorMessage = addressError
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (username.isBlank()) { usernameError = "Required"; isValid = false }
                        if (password.isBlank()) { passwordError = "Required"; isValid = false }
                        if (confirmPassword.isBlank()) { confirmPasswordError = "Required"; isValid = false }
                        if (password != confirmPassword) { confirmPasswordError = "Passwords do not match"; isValid = false }
                        if (name.isBlank()) { nameError = "Required"; isValid = false }
                        if (email.isBlank() || !email.contains("@")) { emailError = "Valid email Required"; isValid = false }
                        if (phone.isBlank()) { phoneError = "Required"; isValid = false }
                        if (age.isBlank()) { ageError = "Required"; isValid = false }
                        if (address.isBlank()) { addressError = "Required"; isValid = false }

                        if (isValid) {
                            val user = UserAccount(
                                user_id = null,
                                username = username,
                                password = password,
                                role_id = 1, // Client
                                status = "ACTIVE",
                                customer_id = null,
                                name = name,
                                email = email,
                                phone = phone.toLongOrNull(),
                                age = age.toIntOrNull(),
                                address = address
                            )
                            viewModel.createUser(user)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal by clicking outside */ },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Account Created!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Please save your User ID. You will need it to log in.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = successUserId,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(successUserId))
                            }) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Go to Login", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
