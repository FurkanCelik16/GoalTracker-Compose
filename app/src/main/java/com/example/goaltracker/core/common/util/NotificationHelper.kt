package com.example.goaltracker.core.common.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.goaltracker.MainActivity
import com.example.goaltracker.R

object NotificationHelper {
    private const val CHANNEL_ID = "goal_reminders"
    private const val CHANNEL_NAME = "Hedef Hatırlatıcıları"

    fun showNotification(context: Context, goalId: Int, string: String, string1: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Hedef hatırlatmaları"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "notification_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hedefini Unutma!")
            .setContentText("Bugünkü hedeflerini tamamlamak için harika bir zaman.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(goalId, notification)
    }
}