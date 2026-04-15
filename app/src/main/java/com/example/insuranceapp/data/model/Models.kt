package com.example.insuranceapp.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Customer(
    val customer_id: String? = null,
    val name: String,
    val email: String? = null,
    val phone: Long,
    val age: Int,
    val address: String,
    val claims: List<String> = emptyList()
)

@Serializable
data class CustomerRequest(
    val name: String,
    val email: String? = null,
    val phone: Long,
    val age: Int,
    val address: String
)

@Serializable
data class Claim(
    @SerialName("claim_id")
    val Claim_ID: String? = null,
    val policy_id: String,
    val claim_date: String,
    val hospital_id: String,
    val claim_amount: Int,
    val status: String,
    val customer_id: String? = null,
    val user_id: String? = null
)

@Serializable
data class Policy(
    val policy_id: String? = null,
    val policy_type: String,
    val premium: Int,
    val coverage_amount: Int,
    val start_date: String,
    val end_date: String
)

@Serializable
data class Payment(
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("payment_id", "Payment_ID", "payment_ID", "Payment_id", "id")
    val payment_id: String? = null,
    val policy_id: String,
    val payment_amount: Int,
    val payment_date: String,
    val payment_mode: String,
    val payment_status: String
)

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String,
    val user_id: String,
    val role: String,
    val customer_id: String? = null
)

@Serializable
data class UserContextResponse(
    val user_id: String,
    val role: String? = null,
    val customer_id: String? = null
)

@Serializable
data class UserAccount(
    val user_id: String? = null,
    val username: String,
    val password: String,
    val role_id: Int,
    val status: String = "ACTIVE",
    val customer_id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: Long? = null,
    val age: Int? = null,
    val address: String? = null
)

@Serializable
data class Role(
    val role_id: Int,
    val role_name: String,
    val description: String
)

@Serializable
data class UserRoleResponse(
    val role: String,
    val user_id: String? = null,
    val customer_id: String? = null
)

enum class AppRole {
    CLIENT, APPROVER, ETL, ADMIN, GUEST;

    companion object {
        fun fromString(role: String?): AppRole {
            return when (role?.lowercase()) {
                "client" -> CLIENT
                "approver" -> APPROVER
                "etl" -> ETL
                "admin" -> ADMIN
                else -> GUEST
            }
        }
    }
}

fun AppRole.canCreateClaim() = this == AppRole.CLIENT || this == AppRole.ADMIN
fun AppRole.canEditClaimDetails() = this == AppRole.CLIENT || this == AppRole.ADMIN
fun AppRole.canEditClaimStatus() = this == AppRole.APPROVER || this == AppRole.ADMIN
fun AppRole.canEditCustomer() = this == AppRole.CLIENT || this == AppRole.ADMIN
fun AppRole.canCreateAnythingExceptClaim() = this == AppRole.ADMIN
fun AppRole.canUpdateAnythingExceptClaimStatus() = this == AppRole.ADMIN
