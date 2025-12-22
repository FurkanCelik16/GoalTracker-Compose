package com.example.goaltracker.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goaltracker.core.data.dao.EntryDao
import com.example.goaltracker.core.data.dao.GoalDao
import com.example.goaltracker.core.data.dao.HabitDao
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitEntry
import com.example.goaltracker.core.util.Converters

@Database(entities = [Habit::class, Goal::class, HabitEntry::class, GoalHistoryEntity::class],
    version = 18,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun entryDao(): EntryDao

}
