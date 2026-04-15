package com.example.insuranceapp.data.repository

import com.example.insuranceapp.data.api.ApiService
import com.example.insuranceapp.data.model.Claim
import com.example.insuranceapp.data.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsuranceRepository(private val apiService: ApiService) {



    // ======================
    // WELCOME
    // ======================
    suspend fun getWelcomeMessage(): Result<String> = safeApiCall {
        apiService.welcomepage()
    }

    // ======================
    // CUSTOMERS
    // ======================
    suspend fun getAllCustomers(): Result<List<Customer>> = safeApiCall {
        apiService.getAllCustomers()
    }

    suspend fun getCustomer(custId: String): Result<Customer?> = safeApiCall {
        val response = apiService.getCustomer(custId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createCustomer(customer: com.example.insuranceapp.data.model.CustomerRequest): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createCustomer(customer)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updateCustomer(custId: String, customer: com.example.insuranceapp.data.model.CustomerRequest): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updateCustomer(custId, customer)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deleteCustomer(custId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deleteCustomer(custId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // CLAIMS
    // ======================
    suspend fun getAllClaims(): Result<List<Claim>> = safeApiCall {
        apiService.getAllClaims()
    }

    suspend fun getClaim(claimId: String): Result<Claim?> = safeApiCall {
        val response = apiService.getClaim(claimId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createClaim(claim: Claim): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createClaim(claim)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updateClaim(claimId: String, claim: Claim): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updateClaim(claimId, claim)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deleteClaim(claimId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deleteClaim(claimId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun getUnassignedClaims(): Result<List<Claim>> = safeApiCall {
        apiService.getUnassignedClaims()
    }

    suspend fun assignClaim(claimId: String, customerId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.assignClaim(mapOf("claim_id" to claimId, "customer_id" to customerId))
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deassignClaim(claimId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deassignClaim(claimId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // POLICIES
    // ======================
    suspend fun getAllPolicies(): Result<List<com.example.insuranceapp.data.model.Policy>> = safeApiCall {
        apiService.getAllPolicies()
    }

    suspend fun getPolicy(policyId: String): Result<com.example.insuranceapp.data.model.Policy?> = safeApiCall {
        val response = apiService.getPolicy(policyId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createPolicy(policy: com.example.insuranceapp.data.model.Policy): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createPolicy(policy)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updatePolicy(policyId: String, policy: com.example.insuranceapp.data.model.Policy): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updatePolicy(policyId, policy)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deletePolicy(policyId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deletePolicy(policyId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // PAYMENTS
    // ======================
    suspend fun getAllPayments(): Result<List<com.example.insuranceapp.data.model.Payment>> = safeApiCall {
        apiService.getAllPayments()
    }

    suspend fun getPayment(paymentId: String): Result<com.example.insuranceapp.data.model.Payment?> = safeApiCall {
        val response = apiService.getPayment(paymentId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createPayment(payment: com.example.insuranceapp.data.model.Payment): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createPayment(payment)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updatePayment(paymentId: String, payment: com.example.insuranceapp.data.model.Payment): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updatePayment(paymentId, payment)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deletePayment(paymentId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deletePayment(paymentId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun login(request: com.example.insuranceapp.data.model.LoginRequest): Result<com.example.insuranceapp.data.model.LoginResponse?> = safeApiCall {
        val response = apiService.login(request)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // USERS
    // ======================
    suspend fun getAllUsers(): Result<List<com.example.insuranceapp.data.model.UserAccount>> = safeApiCall {
        apiService.getAllUsers()
    }

    suspend fun getUser(userId: String): Result<com.example.insuranceapp.data.model.UserAccount?> = safeApiCall {
        val response = apiService.getUser(userId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createUser(user: com.example.insuranceapp.data.model.UserAccount): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createUser(user)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updateUser(userId: String, user: com.example.insuranceapp.data.model.UserAccount): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updateUser(userId, user)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deleteUser(userId: String): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deleteUser(userId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // ROLES
    // ======================
    suspend fun getAllRoles(): Result<List<com.example.insuranceapp.data.model.Role>> = safeApiCall {
        apiService.getAllRoles()
    }

    suspend fun getRole(roleId: Int): Result<com.example.insuranceapp.data.model.Role?> = safeApiCall {
        val response = apiService.getRole(roleId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun createRole(role: com.example.insuranceapp.data.model.Role): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.createRole(role)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun updateRole(roleId: Int, role: com.example.insuranceapp.data.model.Role): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.updateRole(roleId, role)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    suspend fun deleteRole(roleId: Int): Result<Map<String, String>?> = safeApiCall {
        val response = apiService.deleteRole(roleId)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // USER CONTEXT
    // ======================
    suspend fun getUserContext(identifier: String): Result<com.example.insuranceapp.data.model.UserContextResponse?> = safeApiCall {
        val response = apiService.getUserContext(identifier)
        if (response.isSuccessful) response.body()
        else throw Exception(response.message())
    }

    // ======================
    // SAFE API CALL
    // ======================
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(apiCall())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}