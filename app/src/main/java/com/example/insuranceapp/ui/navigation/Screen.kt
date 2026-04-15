package com.example.insuranceapp.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home") // Landing Screen
    object Login : Screen("login")
    object AccountDetail : Screen("account_detail")
    object Dashboard : Screen("dashboard") // Hub Page with options
    object DataExplorer : Screen("data_explorer") // Dropdown-based data viewer
    object QuickStats : Screen("quick_stats")
    object Reports : Screen("reports")
    object Notifications : Screen("notifications")
    object CustomerDetail : Screen("customer_detail/{custId}") {
        fun createRoute(custId: String) = "customer_detail/${Uri.encode(custId)}"
    }
    object CustomerForm : Screen("customer_form?custId={custId}") {
        fun createRoute(custId: String? = null) = if (custId != null) "customer_form?custId=${Uri.encode(custId)}" else "customer_form"
    }
    object ClaimDetail : Screen("claim_detail/{claimId}") {
        fun createRoute(claimId: String) = "claim_detail/${Uri.encode(claimId)}"
    }
    object ClaimForm : Screen("claim_form?claimId={claimId}") {
        fun createRoute(claimId: String? = null) = if (claimId != null) "claim_form?claimId=${Uri.encode(claimId)}" else "claim_form"
    }

    // Policy
    object PolicyDetail : Screen("policy_detail/{policyId}") {
        fun createRoute(policyId: String) = "policy_detail/${Uri.encode(policyId)}"
    }
    object PolicyForm : Screen("policy_form?policyId={policyId}") {
        fun createRoute(policyId: String? = null) = if (policyId != null) "policy_form?policyId=${Uri.encode(policyId)}" else "policy_form"
    }

    // Payment
    object PaymentDetail : Screen("payment_detail/{paymentId}") {
        fun createRoute(paymentId: String) = "payment_detail/${Uri.encode(paymentId)}"
    }
    object PaymentForm : Screen("payment_form?paymentId={paymentId}") {
        fun createRoute(paymentId: String? = null) = if (paymentId != null) "payment_form?paymentId=${Uri.encode(paymentId)}" else "payment_form"
    }

    // User
    object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: String) = "user_detail/${Uri.encode(userId)}"
    }
    object UserForm : Screen("user_form?userId={userId}") {
        fun createRoute(userId: String? = null) = if (userId != null) "user_form?userId=${Uri.encode(userId)}" else "user_form"
    }

    // Role
    object RoleDetail : Screen("role_detail/{roleId}") {
        fun createRoute(roleId: Int) = "role_detail/$roleId"
    }
    object RoleForm : Screen("role_form?roleId={roleId}") {
        fun createRoute(roleId: Int? = null) = if (roleId != null) "role_form?roleId=$roleId" else "role_form"
    }

    object Signup : Screen("signup")
    object Settings : Screen("settings")
}
