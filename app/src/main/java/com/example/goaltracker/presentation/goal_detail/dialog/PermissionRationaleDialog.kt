package com.example.goaltracker.presentation.goal_detail.dialog

import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(androidx.compose.material.icons.Icons.Default.Notifications, contentDescription = null) },
        title = { Text(text = "Bildirim İzni Gerekli ") },
        text = {
            Text(text = "Hedeflerini sana zamanında hatırlatabilmemiz için bildirim iznine ihtiyacımız var.")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("İzin Ver")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Şimdilik Geç")
            }
        }
    )
}