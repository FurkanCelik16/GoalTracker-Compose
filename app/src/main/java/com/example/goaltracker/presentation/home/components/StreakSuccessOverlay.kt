package com.example.goaltracker.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.goaltracker.R
import com.example.goaltracker.core.theme.WarningColor
import kotlinx.coroutines.delay

@Composable
fun StreakSuccessOverlay(
    isVisible: Boolean,
    streak: Int,
    onAnimationFinished: () -> Unit
) {
    val highlightColor = WarningColor
    val messageColor = MaterialTheme.colorScheme.onPrimary

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val message = remember(streak) {
        when {

            streak == 365 -> "BİR YIL OLDU! 🎉\nArtık Sen Bir Efsanesin 👑"

            streak > 365  ->  "Tam ${streak/365} Yıllık Seri! Bu İnanılmaz! \uD83C\uDFC6"

            streak == 90 -> "90 GÜN DEVİRDİN!\nBu Artık Senin Yaşam Tarzın 💪"

            streak > 90 ->  "Tam ${streak/30} Aylık Seri! Harika İlerliyorsun! \uD83C\uDF1F"

            streak >= 30 -> "Tam $streak Günlük Seri!\nGözlerime İnanamıyorum 😱"

            streak >= 21 -> "Durdurulamıyorsun! 🚀"

            streak >= 14 -> "Hızına Yetişemiyoruz! 🤯"

            streak >= 7 -> "Efsanesin. Devam ET! 🔥"

            streak >= 3 -> "Seri Yakalandı! ⚡"

            else -> "Harika Başlangıç! 🌱"
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2000)
            onAnimationFinished()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "$streak GÜN",
                    style = MaterialTheme.typography.headlineLarge,
                    color = messageColor
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = highlightColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}