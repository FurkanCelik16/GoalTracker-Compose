package com.example.goaltracker.core.common.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    text: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

              Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF7272CC), Color(0xFF8EB0FA))
                        )),

                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.weight(0.07f))
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = surfaceColor,
            scrolledContainerColor = surfaceColor,
            actionIconContentColor = contentColor
        ),
        modifier = Modifier
            .height(72.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        windowInsets = WindowInsets(0.dp)
    )
}