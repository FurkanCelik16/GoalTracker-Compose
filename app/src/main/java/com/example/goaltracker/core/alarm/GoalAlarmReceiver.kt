package com.example.goaltracker.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.goaltracker.R
import com.example.goaltracker.core.common.util.NotificationHelper
import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.model.ReminderType
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GoalAlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getIntExtra("GOAL_ID", -1)
        val defaultTitle = context.getString(R.string.notification_default_title)
        val title = intent.getStringExtra("TITLE") ?: defaultTitle

        if (goalId == -1) return

        Log.d("GoalAlarmReceiver", "Alarm Tetiklendi! ID: $goalId")

        val message = context.getString(R.string.notification_default_message)

        NotificationHelper.showNotification(
            context,
            goalId,
            title,
            message
        )
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GoalAlarmReceiverEntryPoint::class.java
        )
        val repository = entryPoint.getRepository()

        val pendingResult = goAsync()

        scope.launch {
            try {
                val goal = repository.getGoalById(goalId).first()

                if (goal != null && !goal.isArchived && goal.reminderType != ReminderType.NONE) {
                    AlarmScheduler.scheduleAlarm(context, goal)
                }
            } catch (e: Exception) {
                Log.e("GoalAlarmReceiver", "Hata: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GoalAlarmReceiverEntryPoint {
        fun getRepository(): GoalRepository
    }
}