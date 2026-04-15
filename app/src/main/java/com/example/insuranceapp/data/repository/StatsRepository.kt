package com.example.insuranceapp.data.repository

import com.example.insuranceapp.data.model.AdminInsights
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StatsRepository {

    private val database = FirebaseDatabase.getInstance()
    private val adminInsightsRef = database.getReference("admin_insights")
    private val connectionKeyRef = database.getReference("KEY")

    fun getConnectionKey(): Flow<String?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(String::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        connectionKeyRef.addValueEventListener(listener)
        awaitClose { connectionKeyRef.removeEventListener(listener) }
    }

    fun getAdminInsights(): Flow<Result<AdminInsights>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val insights = snapshot.getValue(AdminInsights::class.java)
                    if (insights != null) {
                        trySend(Result.success(insights))
                    } else {
                        trySend(Result.failure(Exception("Data is null")))
                    }
                } catch (e: Exception) {
                    trySend(Result.failure(e))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }

        adminInsightsRef.addValueEventListener(listener)

        awaitClose {
            adminInsightsRef.removeEventListener(listener)
        }
    }
}
