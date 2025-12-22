package com.example.goaltracker.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name:String,
    val category:String,
    val period:Period,
    val detail:String = "",
    val streak:Int = 0,
    val difficulty:HabitDifficulty = HabitDifficulty.MEDIUM,
    val type:HabitType = HabitType.POSITIVE,
    val timeOfDay:TimeOfDay = TimeOfDay.ANYTIME,
    val isChallenge: Boolean = false,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val selectedDays: List<Int> = emptyList(),
    val periodInterval: Int = 1,
    val startDate: LocalDate = LocalDate.now(),
    val targetCount: Int = 1,
)