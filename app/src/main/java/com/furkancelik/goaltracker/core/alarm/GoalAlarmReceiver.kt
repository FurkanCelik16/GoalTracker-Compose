package com.furkancelik.goaltracker.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.furkancelik.goaltracker.R
import com.furkancelik.goaltracker.core.common.util.NotificationHelper
import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.model.ReminderType
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
        if (goalId == -1) return

        Log.d("GoalAlarmReceiver", "Alarm Tetiklendi! ID: $goalId")

        val message = context.getString(R.string.notification_default_message)

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GoalAlarmReceiverEntryPoint::class.java
        )
        val repository = entryPoint.getRepository()

        val pendingResult = goAsync()

        scope.launch {
            try {
                val goal = repository.getGoalById(goalId).first()

                // A deleted or disabled goal may still have a stale alarm; drop it silently.
                if (goal != null && !goal.isArchived && goal.reminderType != ReminderType.NONE) {
                    NotificationHelper.showNotification(context, goalId, goal.title, message)
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