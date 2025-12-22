package com.example.goaltracker.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.goaltracker.core.common.util.NotificationHelper
import com.example.goaltracker.core.domain.usecase.goal.GetGoalDetailUseCase
import com.example.goaltracker.core.model.ReminderType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class GoalReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getGoalUseCase: GetGoalDetailUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val goalId = inputData.getInt("goalId", -1)
        if (goalId == -1) return Result.failure()

        val goal = try {
            getGoalUseCase(goalId).first()
        } catch (e: Exception) {
            null
        }

        if (goal == null || goal.reminderType == ReminderType.NONE) {
            return Result.success()
        }

        NotificationHelper.showNotification(
            applicationContext,
            goal.id,
            "Hatırlatıcı: ${goal.title}",
            "Hedefine ulaşmak için harekete geçme zamanı! 🚀"
        )

        scheduleNextReminder(
            applicationContext,
            goal.id,
            goal.reminderType,
            goal.reminderStartTime,
            goal.reminderEndTime,
            goal.reminderIntervalHours
        )

        return Result.success()
    }

    companion object {
        fun scheduleNextReminder(
            context: Context,
            goalId: Int,
            type: ReminderType,
            startTimeStr: String?,
            endTimeStr: String?,
            intervalHours: Int
        ) {
            if (startTimeStr == null) return

            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val startTime = try { LocalTime.parse(startTimeStr, formatter) } catch (e: Exception) { return }

            var nextTriggerTime: LocalDateTime = now

            if (type == ReminderType.ONE_TIME) {
                var target = LocalDateTime.of(now.toLocalDate(), startTime)
                if (target.isBefore(now) || target.isEqual(now)) {
                    target = target.plusDays(1)
                }
                nextTriggerTime = target

            } else if (type == ReminderType.INTERVAL) {
                val endTime = try { LocalTime.parse(endTimeStr ?: "23:59", formatter) } catch (e: Exception) { LocalTime.MAX }

                val potentialNext = now.plusHours(intervalHours.toLong())

                if (potentialNext.toLocalTime().isAfter(endTime)) {
                    nextTriggerTime = LocalDateTime.of(now.toLocalDate().plusDays(1), startTime)
                } else {
                    val todayStart = LocalDateTime.of(now.toLocalDate(), startTime)
                    if (now.isBefore(todayStart)) {
                        nextTriggerTime = todayStart
                    } else {
                        nextTriggerTime = potentialNext
                    }
                }
            }

            val delayInMillis = ChronoUnit.MILLIS.between(now, nextTriggerTime)

            val data = workDataOf("goalId" to goalId)
            val workRequest = OneTimeWorkRequestBuilder<GoalReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delayInMillis.coerceAtLeast(0), TimeUnit.MILLISECONDS)
                .addTag("reminder_$goalId")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "work_$goalId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

            println("Alarm kuruldu: $nextTriggerTime")
        }

        fun cancelReminder(context: Context, goalId: Int) {
            WorkManager.getInstance(context).cancelUniqueWork("work_$goalId")
            println("Alarm iptal edildi: work_$goalId")
        }
    }
}