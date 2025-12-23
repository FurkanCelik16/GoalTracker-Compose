package com.example.goaltracker.presentation.challenge.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.core.common.ui.components.DeleteConfirmDialog
import com.example.goaltracker.presentation.challenge.components.StartChallengeDialog
import com.example.goaltracker.presentation.challenge.model.ChallengeDetailViewModel
import com.example.goaltracker.presentation.challenge.model.ChallengeViewModel

@Composable
fun ChallengeDetailScreen(
    onBack: () -> Unit,
    viewModel: ChallengeDetailViewModel = hiltViewModel(),
    challengeViewModel: ChallengeViewModel = hiltViewModel()
) {
    // 1. STATE COLLECTION
    val challengeState by viewModel.challenge.collectAsStateWithLifecycle()
    val activeTitles by challengeViewModel.activeChallengeTitles.collectAsStateWithLifecycle()

    // 2. LOGIC VARIABLES
    val challenge = challengeState ?: return
    val isJoined = activeTitles.contains(challenge.title)
    val hasAnyActiveChallenge = activeTitles.isNotEmpty()

    // 3. UI STATE
    var showStartDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    // 4. CONTENT ÇAĞRISI (Stateless)
    ChallengeDetailContent(
        challenge = challenge,
        isJoined = isJoined,
        hasAnyActiveChallenge = hasAnyActiveChallenge,
        onBack = onBack,
        onJoinClick = {
            if (hasAnyActiveChallenge) {
                showWarningDialog = true
            } else {
                showStartDialog = true
            }
        },
        onLeaveClick = { showLeaveDialog = true }
    )

    // 5. DIALOG YÖNETİMİ
    if (showStartDialog) {
        StartChallengeDialog(
            challenge = challenge,
            onDismiss = { showStartDialog = false },
            onConfirm = {
                challengeViewModel.startChallenge(challenge) {
                    showStartDialog = false
                }
            }
        )
    }
    if (showLeaveDialog) {
        DeleteConfirmDialog(
            onDismissDelete = { showLeaveDialog = false },
            onConfirmDelete = {
                challengeViewModel.cancelChallenge(challenge) {
                    showLeaveDialog = false
                }
            },
            itemName = challenge.title,
            itemType = "mücadelesini ve tüm ilerlemeni"
        )
    }
    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Odaklanma Zamanı! \uD83D\uDCA1", fontWeight = FontWeight.Bold) },
            text = { Text("Aynı anda sadece bir mücadeleye katılabilirsin. Mevcut mücadeleni bitirmeden veya iptal etmeden yenisine başlayamazsın.") },
            confirmButton = {
                Button(onClick = { showWarningDialog = false }) {
                    Text("Anladım")
                }
            },
            containerColor = Color.DarkGray,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}