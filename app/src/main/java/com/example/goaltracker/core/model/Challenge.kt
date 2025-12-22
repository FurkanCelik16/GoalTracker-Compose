package com.example.goaltracker.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.goaltracker.core.theme.*

data class GoalBlueprint(
    val title: String,
    val targetAmount: Float,
    val type: GoalType,
    val iconIndex: Int
)

data class Challenge(
    val id: Int,
    val title: String,
    val description: String,
    val days: Int,
    val color: Color,
    val goalsToAdd: List<GoalBlueprint>,
    val keyResults: List<Pair<ImageVector, String>>,
    val timeline: List<Pair<String, String>>
)

val sampleChallenges = listOf(
    Challenge(
        id = 1,
        title = "Dopamin Detoksu",
        description = "Beynini sıfırla, odaklanmanı geri kazan. Sosyal medya yok, şeker yok.",
        days = 7,
        color = ChallengePurple,
        goalsToAdd = listOf(
            GoalBlueprint("Sosyal Medya Yok", 1f, GoalType.BINARY, 5),
            GoalBlueprint("Şeker Yok", 1f, GoalType.BINARY, 2),
            GoalBlueprint("Soğuk Duş", 1f, GoalType.BINARY, 8)
        ),
        keyResults = listOf(
            Icons.Default.SelfImprovement to "Zihinsel Berraklık",
            Icons.Default.Timer to "Daha Fazla Zaman",
            Icons.Default.Bedtime to "Kaliteli Uyku",
            Icons.Default.Psychology to "Dürtü Kontrolü"
        ),
        timeline = listOf(
            "1-2. Gün" to "Zorlanma Evresi: Beyin dopamin ister.",
            "3-5. Gün" to "Aydınlanma: Zihin açılmaya başlar.",
            "6-7. Gün" to "Yeni Normal: Küçük şeylerden keyif alırsın."
        )
    ),
    Challenge(
        id = 2,
        title = "Spartacus Modu",
        description = "Vücudunu çeliğe dönüştür. Sınırlarını zorlamaya hazır mısın?",
        days = 21,
        color = ChallengeOrange,
        goalsToAdd = listOf(
            GoalBlueprint("50 Şınav", 50f, GoalType.RECURRING, 0),
            GoalBlueprint("Erken Kalk (06:00)", 1f, GoalType.BINARY, 1),
            GoalBlueprint("3 Litre Su", 3f, GoalType.RECURRING, 3)
        ),
        keyResults = listOf(
            Icons.Default.FitnessCenter to "Fiziksel Güç",
            Icons.Default.LocalFireDepartment to "Yüksek Enerji",
            Icons.Default.Shield to "Demir İrade",
            Icons.Default.AccessibilityNew to "Özgüven"
        ),
        timeline = listOf(
            "1. Hafta" to "Şok Etkisi: Kas ağrıları başlar.",
            "2. Hafta" to "Adaptasyon: Vücut alışır.",
            "3. Hafta" to "Dönüşüm: Aynadaki değişim."
        )
    ),
    Challenge(
        id = 3,
        title = "Zihin Arınması",
        description = "Stresi azalt, farkındalığını artır. Ruhun için bir mola ver.",
        days = 14,
        color = ChallengeGreen,
        goalsToAdd = listOf(
            GoalBlueprint("15 Dk Meditasyon", 15f, GoalType.RECURRING, 4),
            GoalBlueprint("Günlük Tut", 1f, GoalType.BINARY, 6),
            GoalBlueprint("Doğa Yürüyüşü", 1f, GoalType.BINARY, 7)
        ),
        keyResults = listOf(
            Icons.Default.Spa to "İç Huzur",
            Icons.Default.Mood to "Pozitif Bakış",
            Icons.AutoMirrored.Filled.MenuBook to "Farkındalık",
            Icons.Default.BatteryChargingFull to "Ruhsal Enerji"
        ),
        timeline = listOf(
            "1-4. Gün" to "Yavaşlama: Zihni susturmak zor.",
            "5-10. Gün" to "Derinleşme: Kaygı azalır.",
            "11-14. Gün" to "Berrak Zihin: Sakin tepkiler."
        )
    )
)