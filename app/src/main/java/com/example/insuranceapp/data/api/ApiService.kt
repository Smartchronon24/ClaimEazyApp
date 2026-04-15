package com.example.insuranceapp.data.api

import com.example.insuranceapp.data.model.Claim
import com.example.insuranceapp.data.model.Customer
import com.example.insuranceapp.data.model.CustomerRequest
import com.example.insuranceapp.data.model.LoginRequest
import com.example.insuranceapp.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ======================
    // CUSTOMERS
    // ======================
    @GET("/")
    suspend fun welcomepage(): String

    @GET("customers")
    suspend fun getAllCustomers(): List<Customer>

    @GET("customer/{customer_id}")
    suspend fun getCustomer(@Path("customer_id") customerId: String): Response<Customer>

    @POST("customer")
    suspend fun createCustomer(@Body customer: CustomerRequest): Response<Map<String, String>>

    @PUT("customer/{customer_id}")
    suspend fun updateCustomer(
        @Path("customer_id") customerId: String,
        @Body customer: CustomerRequest
    ): Response<Map<String, String>>

    @DELETE("customer/{customer_id}")
    suspend fun deleteCustomer(@Path("customer_id") customerId: String): Response<Map<String, String>>


    // ======================
    // CLAIMS
    // ======================
    @GET("claims")
    suspend fun getAllClaims(): List<Claim>

    @GET("claim/{claim_id}")
    suspend fun getClaim(@Path("claim_id") claimId: String): Response<Claim>

    @POST("claim")
    suspend fun createClaim(@Body claim: Claim): Response<Map<String, String>>

    @PUT("claim/{claim_id}")
    suspend fun updateClaim(
        @Path("claim_id") claimId: String,
        @Body claim: Claim
    ): Response<Map<String, String>>

    @DELETE("claim/{claim_id}")
    suspend fun deleteClaim(@Path("claim_id") claimId: String): Response<Map<String, String>>

    @GET("claims/unassigned")
    suspend fun getUnassignedClaims(): List<Claim>

    @PUT("claims/assign")
    suspend fun assignClaim(
        @Body body: Map<String, String>
    ): Response<Map<String, String>>

    @PUT("claims/deassign/{claim_id}")
    suspend fun deassignClaim(
        @Path("claim_id") claimId: String
    ): Response<Map<String, String>>

    // ======================
    // POLICIES
    // ======================
    @GET("policies")
    suspend fun getAllPolicies(): List<com.example.insuranceapp.data.model.Policy>

    @GET("policy/{policy_id}")
    suspend fun getPolicy(@Path("policy_id") policyId: String): Response<com.example.insuranceapp.data.model.Policy>

    @POST("policy")
    suspend fun createPolicy(@Body policy: com.example.insuranceapp.data.model.Policy): Response<Map<String, String>>

    @PUT("policy/{policy_id}")
    suspend fun updatePolicy(
        @Path("policy_id") policyId: String,
        @Body policy: com.example.insuranceapp.data.model.Policy
    ): Response<Map<String, String>>

    @DELETE("policy/{policy_id}")
    suspend fun deletePolicy(@Path("policy_id") policyId: String): Response<Map<String, String>>

    // ======================
    // PAYMENTS
    // ======================
    @GET("payments")
    suspend fun getAllPayments(): List<com.example.insuranceapp.data.model.Payment>

    @GET("payment/{payment_id}")
    suspend fun getPayment(@Path("payment_id") paymentId: String): Response<com.example.insuranceapp.data.model.Payment>

    @POST("payment")
    suspend fun createPayment(@Body payment: com.example.insuranceapp.data.model.Payment): Response<Map<String, String>>

    @PUT("payment/{payment_id}")
    suspend fun updatePayment(
        @Path("payment_id") paymentId: String,
        @Body payment: com.example.insuranceapp.data.model.Payment
    ): Response<Map<String, String>>

    @DELETE("payment/{payment_id}")
    suspend fun deletePayment(@Path("payment_id") paymentId: String): Response<Map<String, String>>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ======================
    // USERS
    // ======================
    @GET("users")
    suspend fun getAllUsers(): List<com.example.insuranceapp.data.model.UserAccount>

    @GET("user/{user_id}")
    suspend fun getUser(@Path("user_id") userId: String): Response<com.example.insuranceapp.data.model.UserAccount>

    @GET("user/{user_id}/role")
    suspend fun getUserRole(@Path("user_id") userId: String): Response<com.example.insuranceapp.data.model.UserRoleResponse>

    @POST("user")
    suspend fun createUser(@Body user: com.example.insuranceapp.data.model.UserAccount): Response<Map<String, String>>

    @PUT("user/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: String,
        @Body user: com.example.insuranceapp.data.model.UserAccount
    ): Response<Map<String, String>>

    @DELETE("user/{user_id}")
    suspend fun deleteUser(@Path("user_id") userId: String): Response<Map<String, String>>

    // ======================
    // ROLES
    // ======================
    @GET("roles")
    suspend fun getAllRoles(): List<com.example.insuranceapp.data.model.Role>

    @GET("role/{role_id}")
    suspend fun getRole(@Path("role_id") roleId: Int): Response<com.example.insuranceapp.data.model.Role>

    @POST("role")
    suspend fun createRole(@Body role: com.example.insuranceapp.data.model.Role): Response<Map<String, String>>

    @PUT("role/{role_id}")
    suspend fun updateRole(
        @Path("role_id") roleId: Int,
        @Body role: com.example.insuranceapp.data.model.Role
    ): Response<Map<String, String>>

    @DELETE("role/{role_id}")
    suspend fun deleteRole(@Path("role_id") roleId: Int): Response<Map<String, String>>

    // ======================
    // USER CONTEXT
    // ======================
    @GET("user/context/{identifier}")
    suspend fun getUserContext(@Path("identifier") identifier: String): Response<com.example.insuranceapp.data.model.UserContextResponse>
}