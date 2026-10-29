package com.furkancelik.goaltracker.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.furkancelik.goaltracker.core.data.dao.EntryDao
import com.furkancelik.goaltracker.core.data.dao.GoalDao
import com.furkancelik.goaltracker.core.data.dao.HabitDao
import com.furkancelik.goaltracker.core.model.Goal
import com.furkancelik.goaltracker.core.model.GoalHistoryEntity
import com.furkancelik.goaltracker.core.model.Habit
import com.furkancelik.goaltracker.core.model.HabitEntry
import com.furkancelik.goaltracker.core.util.Converters

@Database(entities = [Habit::class, Goal::class, HabitEntry::class, GoalHistoryEntity::class],
    version = 18,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun entryDao(): EntryDao

}
