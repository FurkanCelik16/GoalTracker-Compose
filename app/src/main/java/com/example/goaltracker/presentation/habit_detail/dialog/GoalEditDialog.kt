package com.example.goaltracker.presentation.habit_detail.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.common.ui.components.Input

@Composable
fun GoalEditDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var text by remember { mutableStateOf(currentGoal.toString()) }

    val containerColor = MaterialTheme.colorScheme.surface
    val titleColor = MaterialTheme.colorScheme.onSurface
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Günlük Hedef",
                style = MaterialTheme.typography.titleLarge,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Her gün ulaşmak istediğin XP puanını gir:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(16.dp))

                Input(
                    value = text,
                    onValueChange = { if (it.all { char -> char.isDigit() }) text = it },
                    placeholder = "Örn: 50",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSave(text.toIntOrNull() ?: 50)
                            onDismiss()
                        }
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(text.toIntOrNull() ?: 50)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kaydet", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = textColor, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = containerColor,
        shape = RoundedCornerShape(24.dp)
    )
}