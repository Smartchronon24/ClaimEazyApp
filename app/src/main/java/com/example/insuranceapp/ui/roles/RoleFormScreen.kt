package com.example.insuranceapp.ui.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.insuranceapp.data.model.Role
import com.example.insuranceapp.ui.components.AppTextField
import com.example.insuranceapp.ui.theme.BackgroundGradientDark
import com.example.insuranceapp.ui.theme.BackgroundGradientLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleFormScreen(
    roleId: Int? = null,
    viewModel: RoleViewModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundGradient = if (isDark) BackgroundGradientDark else BackgroundGradientLight
    val isEdit = roleId != null

    var roleName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var manualRoleId by remember { mutableStateOf("") }

    var roleNameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var roleIdError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(roleId) {
        if (isEdit && roleId != null) {
            viewModel.getRole(roleId)
        }
    }

    LaunchedEffect(viewModel.roleDetail) {
        if (isEdit && viewModel.roleDetail != null) {
            val role = viewModel.roleDetail!!
            roleName = role.role_name
            description = role.description
            manualRoleId = role.role_id.toString()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RoleViewModel.UiEvent.NavigateBack -> {
                    onBack()
                }
                is RoleViewModel.UiEvent.ShowSnackbar -> {
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEdit) "Edit Role" else "Create Role") },
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
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AppTextField(
                    value = manualRoleId,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) manualRoleId = it; roleIdError = null
                    },
                    label = "Role ID (Int)",
                    isError = roleIdError != null,
                    errorMessage = roleIdError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    enabled = !isEdit // Usually ID is not editable
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = roleName,
                    onValueChange = { roleName = it; roleNameError = null },
                    label = "Role Name",
                    isError = roleNameError != null,
                    errorMessage = roleNameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = description,
                    onValueChange = { description = it; descriptionError = null },
                    label = "Description",
                    isError = descriptionError != null,
                    errorMessage = descriptionError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (roleName.isBlank()) {
                            roleNameError = "Role name is required"; isValid = false
                        }
                        if (description.isBlank()) {
                            descriptionError = "Description is required"; isValid = false
                        }
                        val roleInt = manualRoleId.toIntOrNull()
                        if (roleInt == null) {
                            roleIdError = "Invalid role ID"; isValid = false
                        }

                        if (isValid) {
                            val role = Role(
                                role_id = roleInt!!,
                                role_name = roleName,
                                description = description
                            )

                            if (isEdit && roleId != null) {
                                viewModel.updateRole(roleId, role)
                            } else {
                                viewModel.createRole(role)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEdit) "Update Role" else "Save Role")
                }
            }
        }
    }
}
