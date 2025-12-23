package com.example.goaltracker.presentation.goal_detail.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.common.ui.components.TimePicker
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.ReminderType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalReminderDialog(
    currentGoal: Goal,
    onDismiss: () -> Unit,
    onSave: (ReminderType, String, String?, Int) -> Unit
) {
    var selectedType by remember { mutableStateOf(currentGoal.reminderType.takeIf { it != ReminderType.NONE } ?: ReminderType.DAILY) }
    var selectedDate by remember {
        mutableStateOf(
            if (currentGoal.reminderType == ReminderType.ONE_TIME &&
                currentGoal.reminderStartTime?.contains("T") == true
            )
                currentGoal.reminderStartTime.substringBefore("T")
            else
                LocalDate.now().toString()
        )
    }

    var startTime by remember {
        mutableStateOf(
            if (currentGoal.reminderStartTime?.contains("T") == true)
                currentGoal.reminderStartTime.substringAfter("T")
            else if (!currentGoal.reminderStartTime.isNullOrBlank())
                currentGoal.reminderStartTime
            else "09:00"
        )
    }

    var endTime by remember {
        mutableStateOf(currentGoal.reminderEndTime ?: "23:00")
    }

    var interval by remember { mutableIntStateOf(currentGoal.reminderIntervalHours.takeIf { it > 0 } ?: 3) }

    var showTimePickerField by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hatırlatıcı Ayarları ⏰") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tip Seçimi
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == ReminderType.ONE_TIME,
                        onClick = { selectedType = ReminderType.ONE_TIME },
                        label = { Text("Tek") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedType == ReminderType.DAILY,
                        onClick = { selectedType = ReminderType.DAILY },
                        label = { Text("Günlük") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedType == ReminderType.INTERVAL,
                        onClick = { selectedType = ReminderType.INTERVAL },
                        label = { Text("Periyot") },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                if (selectedType == ReminderType.ONE_TIME) {
                    Text("Tarih ve Saat Seçimi", style = MaterialTheme.typography.labelLarge)

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text("Tarih") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )
                    Box(modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .clickable { showDatePicker = true })

                    Spacer(modifier = Modifier.height(8.dp))

                    TimeField(
                        time = startTime,
                        label = "Saat",
                        onClick = { showTimePickerField = "START" }
                    )
                }
                if (selectedType == ReminderType.DAILY) {
                    Text("Her Gün Hangi Saatte?", style = MaterialTheme.typography.labelLarge)
                    TimeField(
                        time = startTime,
                        label = "Bildirim Saati",
                        onClick = { showTimePickerField = "START" }
                    )
                }

                if (selectedType == ReminderType.INTERVAL) {
                    Text("Zaman Aralığı ve Sıklık", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) {
                            TimeField(time = startTime, label = "Başlangıç", onClick = { showTimePickerField = "START" })
                        }
                        Box(Modifier.weight(1f)) {
                            TimeField(time = endTime, label = "Bitiş", onClick = { showTimePickerField = "END" })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sıklık: $interval Saatte Bir", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = interval.toFloat(),
                        onValueChange = { interval = it.toInt() },
                        valueRange = 1f..12f,
                        steps = 10
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val finalStartTime = if (selectedType == ReminderType.ONE_TIME) {
                    "${selectedDate}T${startTime}"
                } else {
                    startTime
                }

                val finalEndTime = if (selectedType == ReminderType.INTERVAL) endTime else null

                onSave(selectedType, finalStartTime, finalEndTime, interval)
            }) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            if (currentGoal.reminderType != ReminderType.NONE) {
                TextButton(
                    onClick = { onSave(ReminderType.NONE, "", null, 0) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Kapat") }
            }
            TextButton(onClick = onDismiss) { Text("İptal") }
        },
        shape = RoundedCornerShape(24.dp)
    )

    if (showTimePickerField != null) {
        val currentTime = if (showTimePickerField == "START") startTime else endTime
        val parts = currentTime.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePicker(
            initialHour = h,
            initialMinute = m,
            onDismiss = { showTimePickerField = null },
            onConfirm = { hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                if (showTimePickerField == "START") startTime = formatted else endTime = formatted
                showTimePickerField = null
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        selectedDate = localDate.toString()
                    }
                    showDatePicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun TimeField(time: String, label: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.AccessTime, null) }
        )
        Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
    }
}