package com.example.goaltracker.presentation.challenge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.model.Challenge
import com.example.goaltracker.core.theme.TextWhiteTransparent
import com.example.goaltracker.presentation.challenge.components.ResultItem
import com.example.goaltracker.presentation.challenge.components.TimelineItem

@Composable
fun ChallengeDetailContent(
    challenge: Challenge?,
    isJoined: Boolean,
    hasAnyActiveChallenge: Boolean,
    onBack: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    if (challenge == null) return

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // HEADER BÖLÜMÜ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(challenge.color, challenge.color.copy(alpha = 0.7f))
                        )
                    )
            ) {
                // Geri Butonu
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 48.dp, start = 16.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                }

                // Aksiyon Butonu (Katıl / Ayrıl)
                if (!isJoined) {
                    val buttonColor = if (hasAnyActiveChallenge) Color.Gray else Color.White.copy(alpha = 0.2f)
                    val buttonText = if (hasAnyActiveChallenge) "MÜCADELE AKTİF" else "MÜCADELEYİ BAŞLAT"
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier
                            .padding(top = 48.dp, end = 16.dp)
                            .align(Alignment.TopEnd)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onLeaveClick,
                        modifier = Modifier
                            .padding(top = 48.dp, end = 16.dp)
                            .align(Alignment.TopEnd)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PES ET / BIRAK",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Başlık ve Açıklama
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${challenge.days} GÜNLÜK PROGRAM",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhiteTransparent
                    )
                }
            }

            // ANA SONUÇLAR
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "ANA SONUÇLAR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                challenge.keyResults.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowItems.forEach { (icon, text) -> ResultItem(icon, text, modifier = Modifier.weight(1f)) }
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(text = "YOLCULUĞUNUZ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                challenge.timeline.forEachIndexed { index, (time, desc) ->
                    TimelineItem(time = time, description = desc, isLast = index == challenge.timeline.lastIndex, color = challenge.color)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}