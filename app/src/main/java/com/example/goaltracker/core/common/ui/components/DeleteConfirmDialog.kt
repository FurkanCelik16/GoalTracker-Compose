package com.example.goaltracker.core.common.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.goaltracker.core.theme.RedEnd

@Composable
fun DeleteConfirmDialog(
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    itemName: String,
    itemType: String,
    warningMessage: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismissDelete,
        title = {
            Text(
                text = if (warningMessage != null) "⚠️ Kritik Uyarı" else "Silmek İstediğine Emin misin?",
                fontWeight = FontWeight.Bold,
                color = if (warningMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            if (warningMessage != null) {
                Text(text = warningMessage, color = MaterialTheme.colorScheme.onSurface)
            } else {
                Text(text = "'$itemName' $itemType silmek üzeresin. Bu işlem geri alınamaz.", color = Color.LightGray)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = RedEnd)
            ) {
                Text("Sil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDelete) {
                Text("Vazgeç")
            }
        }
    )
}