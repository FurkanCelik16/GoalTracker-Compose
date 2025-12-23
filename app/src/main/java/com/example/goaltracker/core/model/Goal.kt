package com.example.goaltracker.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName="goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val title:String,
    val type: GoalType = GoalType.RECURRING,
    val targetAmount:Float,
    val currentAmount:Float=0f,
    val iconIndex:Int =0,
    val endDate: LocalDate,
    val lastUpdateDate: LocalDate? = null,
    val isInteger: Boolean = false,
    val unit: String = "",
    val isArchived: Boolean = false,
    val reminderType: ReminderType = ReminderType.NONE,
    val reminderStartTime: String? = null,
    val reminderEndTime: String? = null,
    val reminderIntervalHours: Int = 0,
    val isChallenge: Boolean = false,
    val isChallengeMaster: Boolean = false,
    val parentChallengeTitle: String? = null,
    val startDate: LocalDate = LocalDate.now()
)