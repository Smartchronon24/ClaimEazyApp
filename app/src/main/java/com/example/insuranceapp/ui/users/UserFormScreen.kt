package com.example.insuranceapp.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.UserAccount
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.components.AppDropdown
import com.example.insuranceapp.data.model.LocationConstants
import com.example.insuranceapp.ui.roles.RoleViewModel
import com.example.insuranceapp.ui.roles.RoleState
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    userId: String? = null,
    viewModel: UserAccountViewModel,
    roleViewModel: RoleViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val isEdit = userId != null

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var roleId by remember { mutableStateOf<Int?>(null) }
    var selectedRoleName by remember { mutableStateOf("Select Role") }
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("ACTIVE") }
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAge by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var roleIdError by remember { mutableStateOf<String?>(null) }
    var customerNameError by remember { mutableStateOf<String?>(null) }
    var customerEmailError by remember { mutableStateOf<String?>(null) }
    var customerPhoneError by remember { mutableStateOf<String?>(null) }
    var customerAgeError by remember { mutableStateOf<String?>(null) }
    var customerAddressError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        roleViewModel.loadRoles()
    }

    LaunchedEffect(userId) {
        if (isEdit && userId != null) {
            viewModel.getUser(userId)
        }
    }

    val rolesState = roleViewModel.state

    LaunchedEffect(viewModel.userDetail, rolesState) {
        if (isEdit && viewModel.userDetail != null && rolesState is RoleState.Success) {
            val user = viewModel.userDetail!!
            username = user.username
            password = user.password
            roleId = user.role_id
            selectedRoleName = rolesState.roles.find { it.role_id == user.role_id }?.role_name ?: "Role ID: ${user.role_id}"
            status = user.status
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserAccountViewModel.UiEvent.NavigateBack,
                is UserAccountViewModel.UiEvent.SignupSuccess -> {
                    onBack()
                }
                is UserAccountViewModel.UiEvent.ShowSnackbar -> {
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
                    title = { Text(if (isEdit) "Edit User" else "Create User") },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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
                    isError = passwordError != null,
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(imeAction = if (selectedRoleName.equals("Client", ignoreCase = true)) ImeAction.Next else ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        onDone = { focusManager.clearFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Role Dropdown
                Text(
                    "Role",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedRoleName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Search & Select Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        isError = roleIdError != null,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        when (rolesState) {
                            is RoleState.Loading -> {
                                DropdownMenuItem(
                                    text = {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    onClick = {}
                                )
                            }
                            is RoleState.Error -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Error loading roles",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = { roleViewModel.loadRoles() }
                                )
                            }
                            is RoleState.Success -> {
                                rolesState.roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    role.role_name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    role.description,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        },
                                        onClick = {
                                            roleId = role.role_id
                                            selectedRoleName = role.role_name
                                            roleDropdownExpanded = false
                                            roleIdError = null
                                        },
                                        trailingIcon = {
                                            if (roleId == role.role_id) {
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
                            else -> {}
                        }
                    }
                }
                if (roleIdError != null) {
                    Text(
                        text = roleIdError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Customer Fields for Client Role
                if (selectedRoleName.equals("Client", ignoreCase = true)) {
                    Text(
                        "Client Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    AppTextField(
                        value = customerName,
                        onValueChange = { customerName = it; customerNameError = null },
                        label = "Full Name",
                        isError = customerNameError != null,
                        errorMessage = customerNameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = customerEmail,
                        onValueChange = { customerEmail = it; customerEmailError = null },
                        label = "Email Address",
                        isError = customerEmailError != null,
                        errorMessage = customerEmailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it; customerPhoneError = null },
                        label = "Phone Number",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        isError = customerPhoneError != null,
                        errorMessage = customerPhoneError,
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppTextField(
                            modifier = Modifier.weight(1f),
                            value = customerAge,
                            onValueChange = { customerAge = it; customerAgeError = null },
                            label = "Age",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            isError = customerAgeError != null,
                            errorMessage = customerAgeError,
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                        AppDropdown(
                            modifier = Modifier.weight(2f),
                            label = "Address (State)",
                            options = LocationConstants.INDIAN_STATES,
                            selectedOption = if (customerAddress.isBlank()) "Select State" else customerAddress,
                            onOptionSelected = { customerAddress = it; customerAddressError = null },
                            isError = customerAddressError != null,
                            errorMessage = customerAddressError
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text("Status", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("ACTIVE", "INACTIVE", "BLOCKED").forEach { s ->
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(selected = status == s, onClick = { status = s })
                            Text(s, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (username.isBlank()) {
                            usernameError = "Username is required"; isValid = false
                        }
                        if (password.isBlank()) {
                            passwordError = "Password is required"; isValid = false
                        }
                        if (roleId == null) {
                            roleIdError = "Please select a Role"; isValid = false
                        }

                        val isClient = selectedRoleName.equals("Client", ignoreCase = true)
                        if (isClient) {
                            if (customerName.isBlank()) { customerNameError = "Name is required"; isValid = false }
                            if (customerEmail.isBlank() || !customerEmail.contains("@")) { customerEmailError = "Valid email is required"; isValid = false }
                            if (customerPhone.isBlank()) { customerPhoneError = "Phone is required"; isValid = false }
                            if (customerAge.isBlank()) { customerAgeError = "Age is required"; isValid = false }
                            if (customerAddress.isBlank()) { customerAddressError = "Address is required"; isValid = false }
                        }

                        if (isValid) {
                            val user = UserAccount(
                                user_id = if (isEdit) userId else null,
                                username = username,
                                password = password,
                                role_id = roleId!!,
                                customer_id = null,
                                status = status,
                                name = if (isClient) customerName else null,
                                email = if (isClient) customerEmail else null,
                                phone = if (isClient) customerPhone.toLongOrNull() else null,
                                age = if (isClient) customerAge.toIntOrNull() else null,
                                address = if (isClient) customerAddress else null
                            )

                            if (isEdit && userId != null) {
                                viewModel.updateUser(userId, user)
                            } else {
                                viewModel.createUser(user)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEdit) "Update User Account" else "Save User Account")
                }
            }
        }
    }
}
