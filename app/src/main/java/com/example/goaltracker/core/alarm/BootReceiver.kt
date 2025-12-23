package com.example.goaltracker.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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

class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d("BootReceiver", "Telefon açıldı, alarmlar yeniden kuruluyor...")
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                BootReceiverEntryPoint::class.java
            )
            val repository = entryPoint.getRepository()

            val pendingResult = goAsync()

            scope.launch {
                try {
                    val goals = repository.allGoals.first()
                    var count = 0

                    goals.forEach { goal ->
                        if (goal.reminderType != ReminderType.NONE && !goal.isArchived) {
                            AlarmScheduler.scheduleAlarm(context, goal)
                            count++
                        }
                    }
                    Log.i("BootReceiver", "$count adet alarm başarıyla geri yüklendi.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Alarm geri yükleme hatası: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootReceiverEntryPoint {
        fun getRepository(): GoalRepository
    }
}