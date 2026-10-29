import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BinaryGoalCard(isCompleted: Boolean, onToggleComplete: () -> Unit) {
    val cardColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isCompleted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.EmojiEvents else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = if (isCompleted) MaterialTheme.colorScheme.primary else contentColor
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isCompleted) "Tebrikler! 🎉" else "Henüz Tamamlanmadı",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isCompleted) "Bu hedefi başarıyla gerçekleştirdin." else "Bu hedefe ulaştığında aşağıdaki butona bas.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = contentColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onToggleComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.Delete else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCompleted) "İptal Et / Geri Al" else "HEDEFİ TAMAMLA",
                    color = if (isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}