package com.example.goaltracker.presentation.challenge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.core.model.Challenge
import com.example.goaltracker.presentation.challenge.model.ChallengeViewModel
import com.example.goaltracker.presentation.challenge.components.ChallengeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()

    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }

    if (selectedChallenge == null) {

        Scaffold(
            topBar = {
                TopBar(title = "Mücadele", text = "Sınırlarını Zorla.")
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp)
            ) {
                items(challenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        onClick = { selectedChallenge = challenge }
                    )
                }
            }
        }
    } else {
        ChallengeDetailScreen(
            challenge = selectedChallenge!!,
            onBack = { selectedChallenge = null },
        )
    }
}