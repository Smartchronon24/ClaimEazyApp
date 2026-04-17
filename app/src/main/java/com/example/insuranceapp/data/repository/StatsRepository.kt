package com.example.insuranceapp.data.repository

import com.example.insuranceapp.data.model.AdminInsights
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * F-Droid compatible StatsRepository.
 * Firebase has been removed for FOSS compliance.
 * Connection key is managed manually via the HomeScreen UI.
 * Admin insights are not available in this build.
 */
class StatsRepository {

    /**
     * Connection key is now managed entirely via SharedPreferences
     * and the manual input on HomeScreen. No remote sync.
     */
    fun getConnectionKey(): Flow<String?> = flowOf(null)

    /**
     * Admin insights are not available without Firebase.
     * Returns an error result to let the UI show an appropriate message.
     */
    fun getAdminInsights(): Flow<Result<AdminInsights>> = flowOf(
        Result.failure(Exception("Admin insights are not available in the F-Droid build. Stats are powered by the backend admin panel."))
    )
}
