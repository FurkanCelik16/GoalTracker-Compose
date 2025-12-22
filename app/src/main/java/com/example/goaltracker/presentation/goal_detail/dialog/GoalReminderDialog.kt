package com.example.goaltracker.presentation.goal_detail.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.common.ui.components.TimePicker
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.ReminderType

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalReminderDialog(
    currentGoal: Goal,
    onDismiss: () -> Unit,
    onSave: (ReminderType, String, String?, Int) -> Unit
) {
    var selectedType by remember { mutableStateOf(currentGoal.reminderType.takeIf { it != ReminderType.NONE } ?: ReminderType.ONE_TIME) }

    var startTime by remember {
        mutableStateOf(if (currentGoal.reminderStartTime.isNullOrBlank()) "09:00" else currentGoal.reminderStartTime)
    }
    var endTime by remember {
        mutableStateOf(if (currentGoal.reminderEndTime.isNullOrBlank()) "23:00" else currentGoal.reminderEndTime)
    }

    var interval by remember { mutableIntStateOf(currentGoal.reminderIntervalHours.takeIf { it > 0 } ?: 3) }
    var activeTimeField by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hatırlatıcı Ayarları ⏰") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = selectedType == ReminderType.ONE_TIME,
                        onClick = { selectedType = ReminderType.ONE_TIME },
                        label = { Text("Tek Seferlik") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedType == ReminderType.INTERVAL,
                        onClick = { selectedType = ReminderType.INTERVAL },
                        label = { Text("Periyodik") },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                if (selectedType == ReminderType.ONE_TIME) {
                    Text("Bildirim Saati", style = MaterialTheme.typography.labelLarge)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            label = { Text("Saat") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null,tint = MaterialTheme.colorScheme.primary) }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { activeTimeField = "START" }
                        )
                    }

                } else {
                    Text("Zaman Aralığı", style = MaterialTheme.typography.labelLarge)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = {},
                                label = { Text("Başlangıç") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = true,
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { activeTimeField = "START" }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = {},
                                label = { Text("Bitiş") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = true,
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null,tint = MaterialTheme.colorScheme.primary) }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { activeTimeField = "END" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Sıklık: $interval Saatte Bir", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = interval.toFloat(),
                        onValueChange = { interval = it.toInt() },
                        valueRange = 1f..6f,
                        steps = 4
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(selectedType, startTime, if(selectedType == ReminderType.INTERVAL) endTime else null, interval)
            }) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            if (currentGoal.reminderType != ReminderType.NONE) {
                TextButton(
                    onClick = { onSave(ReminderType.NONE, "", null, 0) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Alarmı Kapat")
                }
            }
            TextButton(onClick = onDismiss) { Text("İptal") }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )

    if (activeTimeField != null) {
        val currentTime = if (activeTimeField == "START") startTime else endTime
        val parts = currentTime.split(":")
        val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePicker(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { activeTimeField = null },
            onConfirm = { hour, minute ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                if (activeTimeField == "START") {
                    startTime = formattedTime
                } else {
                    endTime = formattedTime
                }
                activeTimeField = null
            }
        )
    }
}