package com.oolestudio.tamashi.ui.screens.pet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oolestudio.tamashi.data.TamashiLevel

/**
 * Obtiene el emoji de estado de salud
 */
private fun getHealthEmoji(healthPercentage: Float): String {
    return when {
        healthPercentage >= 0.9f -> "😄"
        healthPercentage >= 0.8f -> "😊"
        healthPercentage >= 0.7f -> "🙂"
        healthPercentage >= 0.6f -> "😐"
        else -> "😰"
    }
}

/**
 * Obtiene el emoji del nivel
 */
private fun getLevelEmoji(level: TamashiLevel): String {
    return when (level) {
        TamashiLevel.BABY -> "🍼"
        TamashiLevel.CHILD -> "🧒"
        TamashiLevel.YOUNG -> "🌟"
        TamashiLevel.ADULT -> "💪"
        TamashiLevel.MASTER -> "👑"
    }
}

@Composable
fun TamashiStatsCard(
    tamashiLevel: TamashiLevel,
    healthPercentage: Float,
    xpProgress: Float,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedHealth by animateFloatAsState(
        targetValue = healthPercentage,
        animationSpec = tween(durationMillis = 500),
        label = "health"
    )

    val healthColor by animateColorAsState(
        targetValue = when {
            healthPercentage >= 0.8f -> MaterialTheme.colorScheme.primary
            healthPercentage >= 0.6f -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(durationMillis = 300),
        label = "healthColor"
    )

    val animatedXpProgress by animateFloatAsState(
        targetValue = xpProgress,
        animationSpec = tween(durationMillis = 500),
        label = "xpProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getLevelEmoji(tamashiLevel),
                    fontSize = 28.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Nivel ${tamashiLevel.levelNumber}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(onClick = onInfoClick) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Información de Salud"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Health Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Salud",
                    tint = healthColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedHealth)
                            .height(20.dp)
                            .clip(CircleShape)
                            .background(healthColor)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = getHealthEmoji(healthPercentage), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // XP Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "XP",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedXpProgress)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}
