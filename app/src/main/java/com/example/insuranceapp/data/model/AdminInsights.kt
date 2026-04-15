package com.example.insuranceapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminInsights(
    val claims: ClaimInsights? = null,
    val customers: CustomerInsights? = null,
    val payments: PaymentInsights? = null,
    val policies: PolicyInsights? = null,
    val users: UserInsights? = null
)

@Serializable
data class ClaimInsights(
    val by_status: Map<String, Int>? = null,
    val total: Int = 0,
    val totalclaimed_amt: Long = 0
)

@Serializable
data class CustomerInsights(
    val age_group: Map<String, Int>? = null,
    val address: Map<String, Int>? = null
)

@Serializable
data class PaymentInsights(
    val by_status: Map<String, Int>? = null,
    val payment_mode: Map<String, Int>? = null,
    val pending: Long = 0,
    val total_revenue: Long = 0
)

@Serializable
data class PolicyInsights(
    val active: Int = 0,
    val expired: Int = 0,
    val upcoming: Int = 0
)

@Serializable
data class UserInsights(
    val by_role: Map<String, Int>? = null,
    val total: Int = 0
)
