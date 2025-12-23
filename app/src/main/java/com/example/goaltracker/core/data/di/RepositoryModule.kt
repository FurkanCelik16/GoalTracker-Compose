package com.example.goaltracker.core.data.di

import com.example.goaltracker.core.data.repository.AnalysisRepositoryImpl
import com.example.goaltracker.core.data.repository.GoalRepositoryImpl
import com.example.goaltracker.core.data.repository.HabitRepositoryImpl
import com.example.goaltracker.core.data.repository.SettingsRepositoryImpl
import com.example.goaltracker.core.domain.repository.AnalysisRepository
import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(impl: AnalysisRepositoryImpl): AnalysisRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}