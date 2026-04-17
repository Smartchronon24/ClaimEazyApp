package com.example.insuranceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.insuranceapp.ui.claims.*
import com.example.insuranceapp.ui.customers.*
import com.example.insuranceapp.ui.policies.*
import com.example.insuranceapp.ui.payments.*
import com.example.insuranceapp.ui.users.*
import com.example.insuranceapp.ui.roles.*
import com.example.insuranceapp.ui.main.DashboardScreen
import com.example.insuranceapp.ui.main.DataExplorerScreen
import com.example.insuranceapp.ui.main.HomeScreen
import com.example.insuranceapp.ui.main.HomeViewModel
import com.example.insuranceapp.ui.main.QuickStatsViewModel
import com.example.insuranceapp.ui.main.ThemeViewModel
import com.example.insuranceapp.ui.main.SettingsScreen
import com.example.insuranceapp.ui.main.QuickStatsScreen
import com.example.insuranceapp.ui.main.ReportsScreen
import com.example.insuranceapp.ui.main.NotificationsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    themeViewModel: ThemeViewModel,
    customerViewModel: CustomerViewModel,
    claimViewModel: ClaimViewModel,
    policyViewModel: PolicyViewModel,
    paymentViewModel: PaymentViewModel,
    userViewModel: UserAccountViewModel,
    roleViewModel: RoleViewModel,
    quickStatsViewModel: QuickStatsViewModel,
    isDark: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                isDark = isDark,
                onExploreClick = { 
                    if (userViewModel.isLoggedIn || userViewModel.isGuest) {
                        navController.navigate(Screen.Dashboard.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = userViewModel,
                isDark = isDark,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                viewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = userViewModel,
                isDark = isDark,
                onViewDetailsClick = { navController.navigate(Screen.DataExplorer.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onQuickStatsClick = { navController.navigate(Screen.QuickStats.route) },
                onReportsClick = { navController.navigate(Screen.Reports.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onAccountClick = { navController.navigate(Screen.AccountDetail.route) },
                onSignInClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.DataExplorer.route) {
            DataExplorerScreen(
                customerViewModel = customerViewModel,
                claimViewModel = claimViewModel,
                policyViewModel = policyViewModel,
                paymentViewModel = paymentViewModel,
                userViewModel = userViewModel,
                roleViewModel = roleViewModel,
                isDark = isDark,
                onCustomerClick = { navController.navigate(Screen.CustomerDetail.createRoute(it)) },
                onAddCustomerClick = { navController.navigate(Screen.CustomerForm.createRoute()) },
                onClaimClick = { navController.navigate(Screen.ClaimDetail.createRoute(it)) },
                onAddClaimClick = { navController.navigate(Screen.ClaimForm.createRoute()) },
                onPolicyClick = { navController.navigate(Screen.PolicyDetail.createRoute(it)) },
                onAddPolicyClick = { navController.navigate(Screen.PolicyForm.createRoute()) },
                onPaymentClick = { navController.navigate(Screen.PaymentDetail.createRoute(it)) },
                onAddPaymentClick = { navController.navigate(Screen.PaymentForm.createRoute()) },
                onUserClick = { navController.navigate(Screen.UserDetail.createRoute(it)) },
                onAddUserClick = { navController.navigate(Screen.UserForm.createRoute()) },
                onRoleClick = { navController.navigate(Screen.RoleDetail.createRoute(it)) },
                onAddRoleClick = { navController.navigate(Screen.RoleForm.createRoute()) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("custId") { type = NavType.StringType })
        ) { backStackEntry ->
            val custId = backStackEntry.arguments?.getString("custId") ?: return@composable
            CustomerDetailScreen(
                custId = custId,
                viewModel = customerViewModel,
                claimViewModel = claimViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.CustomerForm.createRoute(it)) },
                onClaimClick = { navController.navigate(Screen.ClaimDetail.createRoute(it)) }
            )
        }

        composable(
            route = Screen.CustomerForm.route,
            arguments = listOf(navArgument("custId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val custId = backStackEntry.arguments?.getString("custId")
            CustomerFormScreen(
                custId = custId,
                viewModel = customerViewModel,
                claimViewModel = claimViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ClaimDetail.route,
            arguments = listOf(navArgument("claimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val claimId = backStackEntry.arguments?.getString("claimId") ?: return@composable
            ClaimDetailScreen(
                claimId = claimId,
                viewModel = claimViewModel,
                customerViewModel = customerViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.ClaimForm.createRoute(it)) }
            )
        }

        composable(Screen.ClaimForm.route) { backStackEntry ->
            val claimId = backStackEntry.arguments?.getString("claimId")
            ClaimFormScreen(
                claimId = claimId,
                viewModel = claimViewModel,
                policyViewModel = policyViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        // Policy
        composable(
            route = Screen.PolicyDetail.route,
            arguments = listOf(navArgument("policyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val policyId = backStackEntry.arguments?.getString("policyId") ?: return@composable
            PolicyDetailScreen(
                policyId = policyId,
                viewModel = policyViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.PolicyForm.createRoute(it)) }
            )
        }

        composable(Screen.PolicyForm.route) { backStackEntry ->
            val policyId = backStackEntry.arguments?.getString("policyId")
            PolicyFormScreen(
                policyId = policyId,
                viewModel = policyViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        // Payment
        composable(
            route = Screen.PaymentDetail.route,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId") ?: return@composable
            PaymentDetailScreen(
                paymentId = paymentId,
                viewModel = paymentViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.PaymentForm.createRoute(it)) }
            )
        }

        composable(Screen.PaymentForm.route) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId")
            PaymentFormScreen(
                paymentId = paymentId,
                viewModel = paymentViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        // User
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                viewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.UserForm.createRoute(it)) }
            )
        }

        composable(Screen.UserForm.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserFormScreen(
                userId = userId,
                viewModel = userViewModel,
                roleViewModel = roleViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        // Role
        composable(
            route = Screen.RoleDetail.route,
            arguments = listOf(navArgument("roleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val roleId = backStackEntry.arguments?.getInt("roleId") ?: return@composable
            RoleDetailScreen(
                roleId = roleId,
                viewModel = roleViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.RoleForm.createRoute(it)) }
            )
        }

        composable(
            route = Screen.RoleForm.route,
            arguments = listOf(navArgument("roleId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val roleId = backStackEntry.arguments?.getInt("roleId")
            val id = if (roleId == -1) null else roleId
            RoleFormScreen(
                roleId = id,
                viewModel = roleViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                customerViewModel = customerViewModel,
                claimViewModel = claimViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.QuickStats.route) {
            QuickStatsScreen(
                viewModel = quickStatsViewModel,
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                userViewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                isDark = isDark,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AccountDetail.route) {
            AccountDetailScreen(
                viewModel = userViewModel,
                isDark = isDark,
                onBack = { navController.popBackStack() },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
