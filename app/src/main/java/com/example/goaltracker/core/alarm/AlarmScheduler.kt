package com.example.goaltracker.core.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.ReminderType
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context, goal: Goal) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val intent = Intent(context, GoalAlarmReceiver::class.java).apply {
            putExtra("GOAL_ID", goal.id)
            putExtra("TITLE", goal.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goal.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextTriggerTime = calculateNextTriggerTime(goal)

        if (nextTriggerTime != null) {
            val triggerMillis = nextTriggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            alarmManager.cancel(pendingIntent)

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                Log.e("AlarmScheduler", "İzin hatası: ${e.message}")
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }
    fun cancelAlarm(context: Context, goalId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, GoalAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goalId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    private fun calculateNextTriggerTime(goal: Goal): LocalDateTime? {
        if (goal.reminderStartTime.isNullOrBlank()) return null
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        return try {
            when (goal.reminderType) {
                ReminderType.ONE_TIME -> {
                    val target = LocalDateTime.parse(goal.reminderStartTime)
                    if (target.isAfter(now)) target else null
                }
                ReminderType.DAILY -> {
                    val time = LocalTime.parse(goal.reminderStartTime, formatter)
                    var target = LocalDateTime.of(now.toLocalDate(), time)
                    if (!target.isAfter(now)) target = target.plusDays(1)
                    target
                }
                ReminderType.INTERVAL -> {
                    val start = LocalTime.parse(goal.reminderStartTime, formatter)
                    val end = try { LocalTime.parse(goal.reminderEndTime ?: "23:59", formatter) } catch (_: Exception) { LocalTime.MAX }
                    val interval = if (goal.reminderIntervalHours < 1) 1 else goal.reminderIntervalHours

                    var candidate = LocalDateTime.of(now.toLocalDate(), start)
                    val todayEnd = LocalDateTime.of(now.toLocalDate(), end)

                    while (!candidate.isAfter(now)) {
                        candidate = candidate.plusHours(interval.toLong())
                    }

                    if (candidate.isAfter(todayEnd)) {
                        LocalDateTime.of(now.toLocalDate().plusDays(1), start)
                    } else {
                        candidate
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}