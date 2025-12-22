package com.example.goaltracker.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.goaltracker.core.data.dao.EntryDao
import com.example.goaltracker.core.data.dao.GoalDao
import com.example.goaltracker.core.data.dao.HabitDao
import com.example.goaltracker.core.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "goal_tracker.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideEntryDao(db: AppDatabase): EntryDao=db.entryDao()
}