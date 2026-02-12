package com.example.goaltracker.presentation.habit_detail.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.model.Habit
import java.time.LocalDate

@Composable
fun HeatMapCard(
    completedDates: List<LocalDate>,
    habit: Habit,
    currentDate: LocalDate = LocalDate.now(),
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        HabitHeatMap(
            habitType = habit.type,
            completedDates = completedDates,
            startDate = habit.startDate,
            currentDate = currentDate
        )
    }
}