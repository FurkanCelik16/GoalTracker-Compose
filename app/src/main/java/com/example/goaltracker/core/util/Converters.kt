package com.example.goaltracker.core.util

import androidx.room.TypeConverter
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import com.example.goaltracker.core.model.Period
import com.example.goaltracker.core.model.TimeOfDay
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun fromHabitType(value: HabitType): String = value.name
    @TypeConverter
    fun toHabitType(value: String): HabitType = HabitType.valueOf(value)

    @TypeConverter
    fun fromHabitDifficulty(value: HabitDifficulty): String = value.name
    @TypeConverter
    fun toHabitDifficulty(value: String): HabitDifficulty = HabitDifficulty.valueOf(value)

    @TypeConverter
    fun fromPeriod(value: Period): String = value.name
    @TypeConverter
    fun toPeriod(value: String): Period = Period.valueOf(value)

    @TypeConverter
    fun fromTimeOfDay(value: TimeOfDay): String = value.name
    @TypeConverter
    fun toTimeOfDay(value: String): TimeOfDay = TimeOfDay.valueOf(value)

    @TypeConverter
    fun fromGoalType(value: GoalType): String = value.name

    @TypeConverter
    fun toGoalType(value: String): GoalType {
        return try {
            GoalType.valueOf(value)
        } catch (e: Exception) {
            GoalType.RECURRING
        }
    }
    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return list?.joinToString(separator = ",") { it.toString() } ?: ""
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int> {
        if (data.isNullOrEmpty()) return emptyList()
        return try {
            data.split(",").map { it.toInt() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}