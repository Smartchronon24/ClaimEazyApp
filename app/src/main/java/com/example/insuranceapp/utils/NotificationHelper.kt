package com.example.insuranceapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.insuranceapp.R

object NotificationHelper {
    private const val SIGNUP_CHANNEL_ID = "signup_notifications"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Account Notifications"
            val descriptionText = "Notifications for account creation and updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(SIGNUP_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSignupNotification(context: Context, userId: String) {
        val builder = NotificationCompat.Builder(context, SIGNUP_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using a default system icon for safety
            .setContentTitle("Account Created!")
            .setContentText("Your User ID is $userId. Use it to log in.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Handle permission not granted case gracefully
                e.printStackTrace()
            }
        }
    }
}
