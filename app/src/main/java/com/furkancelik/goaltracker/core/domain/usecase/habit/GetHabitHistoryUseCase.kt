package com.furkancelik.goaltracker.core.domain.usecase.habit

import com.furkancelik.goaltracker.core.domain.repository.AnalysisRepository
import com.furkancelik.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitHistoryUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    operator fun invoke(habitId: Int): Flow<List<HabitEntry>> {
        return repository.getHabitHistory(habitId)
    }
}