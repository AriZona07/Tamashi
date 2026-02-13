package com.oolestudio.tamashi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.oolestudio.tamashi.R
import com.oolestudio.tamashi.data.TamashiLevel
import com.oolestudio.tamashi.viewmodel.HomeViewModel

/**
 * Estado de la animación del Tamashi
 */
private enum class TamashiState {
    EGG_IDLE,      // Huevo temblando (sin objetivos)
    EGG_HATCHING,  // Huevo eclosionando (animación completa una vez)
    AXOLOTL        // Ajolote (después de la eclosión)
}

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
 * Obtiene un mensaje motivacional según el nivel
 */
private fun getLevelMessage(level: TamashiLevel): String {
    return when (level) {
        TamashiLevel.BABY -> "¡Acabo de nacer! Ayúdame a crecer 🌱"
        TamashiLevel.CHILD -> "¡Estoy creciendo! Sigue así 💪"
        TamashiLevel.YOUNG -> "¡Cada vez soy más fuerte! 🌟"
        TamashiLevel.ADULT -> "¡Somos un gran equipo! 🎯"
        TamashiLevel.MASTER -> "¡Juntos somos imparables! 👑"
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

/**
 * Pantalla de la Mascota (Tamashi).
 * Muestra la animación Lottie del Tamashi con transiciones suaves.
 */
@Composable
fun PetScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // Observa los estados del ViewModel
    val totalObjectives by homeViewModel.totalObjectivesCount.collectAsState()
    val completedObjectives by homeViewModel.completedObjectivesCount.collectAsState()
    val shouldShowHatching by homeViewModel.shouldShowHatching.collectAsState()
    val tamashiHasHatched by homeViewModel.tamashiHasHatched.collectAsState()

    // Estados de nivel y XP
    val tamashiXp by homeViewModel.tamashiXp.collectAsState()
    val tamashiLevel by homeViewModel.tamashiLevel.collectAsState()
    val levelUpEvent by homeViewModel.levelUpEvent.collectAsState()

    // Estado para mostrar el diálogo de información
    var showInfoDialog by remember { mutableStateOf(false) }

    // Calcular progreso hacia el siguiente nivel
    val nextLevelXp = com.oolestudio.tamashi.data.TamashiLevel.getNextLevel(tamashiLevel)?.requiredXp
    val xpProgress by remember(tamashiXp, tamashiLevel) {
        derivedStateOf {
            if (nextLevelXp != null) {
                val xpInCurrentLevel = tamashiXp - tamashiLevel.requiredXp
                val xpNeededForNext = nextLevelXp - tamashiLevel.requiredXp
                if (xpNeededForNext > 0) {
                    (xpInCurrentLevel.toFloat() / xpNeededForNext.toFloat()).coerceIn(0f, 1f)
                } else 1f
            } else {
                1f // Nivel máximo
            }
        }
    }

    // Animación del progreso de XP
    val animatedXpProgress by animateFloatAsState(
        targetValue = xpProgress,
        animationSpec = tween(durationMillis = 500),
        label = "xpProgress"
    )

    // Tamaño del Tamashi según el nivel (crece de 150dp a 250dp)
    // Nivel 1 (Bebé) = 150dp, Nivel 5 (Maestro) = 250dp
    val tamashiSize by remember(tamashiLevel) {
        derivedStateOf {
            when (tamashiLevel) {
                com.oolestudio.tamashi.data.TamashiLevel.BABY -> 150f    // Pequeño
                com.oolestudio.tamashi.data.TamashiLevel.CHILD -> 175f   // Un poco más grande
                com.oolestudio.tamashi.data.TamashiLevel.YOUNG -> 200f   // Mediano
                com.oolestudio.tamashi.data.TamashiLevel.ADULT -> 225f   // Grande
                com.oolestudio.tamashi.data.TamashiLevel.MASTER -> 250f  // Máximo
            }
        }
    }

    // Animación suave del tamaño cuando sube de nivel
    val animatedTamashiSize by animateFloatAsState(
        targetValue = tamashiSize,
        animationSpec = tween(durationMillis = 800),
        label = "tamashiSize"
    )

    // Calcula la salud del Tamashi (50% - 100%)
    // Fórmula: 50% base + 50% * (completados / total)
    // Si no hay objetivos, la salud es 100%
    val healthPercentage by remember(totalObjectives, completedObjectives) {
        derivedStateOf {
            if (totalObjectives == 0) {
                1f // 100% si no hay objetivos (recién nacido)
            } else {
                val completionRatio = completedObjectives.toFloat() / totalObjectives.toFloat()
                // Escala de 50% a 100%: 0.5 + (0.5 * completionRatio)
                0.5f + (0.5f * completionRatio)
            }
        }
    }

    // Animación suave del progreso de salud
    val animatedHealth by animateFloatAsState(
        targetValue = healthPercentage,
        animationSpec = tween(durationMillis = 500),
        label = "health"
    )

    // Color de la barra de salud según el nivel
    val healthColor by animateColorAsState(
        targetValue = when {
            healthPercentage >= 0.8f -> Color(0xFF4CAF50) // Verde - salud alta
            healthPercentage >= 0.6f -> Color(0xFFFFC107) // Amarillo - salud media
            else -> Color(0xFFFF5722) // Naranja/Rojo - salud baja
        },
        animationSpec = tween(durationMillis = 300),
        label = "healthColor"
    )

    // Calcula el estado del Tamashi de forma reactiva
    val tamashiState by remember(shouldShowHatching, tamashiHasHatched, totalObjectives) {
        derivedStateOf {
            when {
                shouldShowHatching -> TamashiState.EGG_HATCHING
                tamashiHasHatched || totalObjectives > 0 -> TamashiState.AXOLOTL
                else -> TamashiState.EGG_IDLE
            }
        }
    }

    // Visibilidad de animaciones basada directamente en el estado
    val showEgg = tamashiState == TamashiState.EGG_IDLE || tamashiState == TamashiState.EGG_HATCHING
    val showAxolotl = tamashiState == TamashiState.AXOLOTL

    // Composiciones de Lottie
    val eggComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.egg)
    )

    val axolotlComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.ajolote)
    )

    // Clip para el huevo: solo primer 33% cuando está idle
    val eggClipSpec = when (tamashiState) {
        TamashiState.EGG_IDLE -> LottieClipSpec.Progress(0f, 0.33f)
        TamashiState.EGG_HATCHING -> null // Animación completa
        TamashiState.AXOLOTL -> null
    }

    // Progreso del huevo
    val eggProgress by animateLottieCompositionAsState(
        composition = eggComposition,
        iterations = if (tamashiState == TamashiState.EGG_HATCHING) 1 else LottieConstants.IterateForever,
        clipSpec = eggClipSpec,
        isPlaying = tamashiState != TamashiState.AXOLOTL
    )

    // Detecta cuando la animación de eclosión termina
    LaunchedEffect(eggProgress, tamashiState) {
        if (tamashiState == TamashiState.EGG_HATCHING && eggProgress >= 0.99f) {
            // La animación de eclosión terminó - marcar como completada
            // Esto disparará el cambio de estado a AXOLOTL via derivedStateOf
            homeViewModel.onHatchingComplete()
        }
    }

    // Progreso del ajolote
    val axolotlProgress by animateLottieCompositionAsState(
        composition = axolotlComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = tamashiState == TamashiState.AXOLOTL
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra de vida - Solo visible cuando el Tamashi ha nacido
        if (tamashiState == TamashiState.AXOLOTL) {
            // Card con estadísticas del Tamashi
            Card(
                modifier = Modifier
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
                    // Header con emoji y botón de info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji de nivel
                        Text(
                            text = getLevelEmoji(tamashiLevel),
                            fontSize = 28.sp
                        )

                        // Badge de nivel central
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFEF88B6),
                                            Color(0xFFB388EF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Nv.${tamashiLevel.levelNumber} ${tamashiLevel.displayName}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Botón de información
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Información del Tamashi",
                                tint = Color(0xFFEF88B6),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección de Vida
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Vida",
                            tint = healthColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vida",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${getHealthEmoji(healthPercentage)} ${(healthPercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = healthColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Barra de progreso de vida con gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedHealth)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            healthColor.copy(alpha = 0.7f),
                                            healthColor
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$completedObjectives de $totalObjectives objetivos completados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección de XP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = Color(0xFFEF88B6),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Experiencia",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (nextLevelXp != null) {
                            Text(
                                text = "$tamashiXp / $nextLevelXp XP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF88B6)
                            )
                        } else {
                            Text(
                                text = "✨ MAX",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Barra de progreso de XP con gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedXpProgress)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFEF88B6),
                                            Color(0xFFB388EF)
                                        )
                                    )
                                )
                        )
                    }

                    if (nextLevelXp != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${nextLevelXp - tamashiXp} XP para el siguiente nivel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Contenedor de animaciones con transición
        Box(
            modifier = Modifier.size(250.dp), // Tamaño máximo del contenedor
            contentAlignment = Alignment.Center
        ) {
            // Animación del Huevo (tamaño fijo)
            androidx.compose.animation.AnimatedVisibility(
                visible = showEgg,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                LottieAnimation(
                    composition = eggComposition,
                    progress = { eggProgress },
                    modifier = Modifier.size(200.dp) // Huevo tamaño fijo
                )
            }

            // Animación del Ajolote (tamaño variable según nivel)
            androidx.compose.animation.AnimatedVisibility(
                visible = showAxolotl,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                LottieAnimation(
                    composition = axolotlComposition,
                    progress = { axolotlProgress },
                    modifier = Modifier.size(animatedTamashiSize.dp) // Tamaño crece con el nivel
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mensajes según el estado
        when (tamashiState) {
            TamashiState.EGG_IDLE -> {
                // Mensaje para huevo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🥚",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tu Tamashi está esperando...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crea tu primer objetivo para que pueda nacer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            TamashiState.EGG_HATCHING -> {
                // Mensaje durante eclosión
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉✨🎉",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Tu Tamashi está naciendo!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF88B6)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Un momento mágico...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            TamashiState.AXOLOTL -> {
                // Mensaje personalizado según nivel y salud
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mensaje dinámico según el nivel
                    Text(
                        text = getLevelMessage(tamashiLevel),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEF88B6),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mensaje de estado basado en salud
                    val statusMessage = when {
                        healthPercentage >= 0.9f -> "¡Me siento increíble! Sigue así 🌈"
                        healthPercentage >= 0.7f -> "¡Estamos haciéndolo bien! 💫"
                        healthPercentage >= 0.5f -> "Podemos mejorar juntos 🌱"
                        else -> "¡Necesito que completes objetivos! 🆘"
                    }

                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Diálogo de información del Tamashi
    if (showInfoDialog) {
        TamashiInfoDialog(
            currentLevel = tamashiLevel,
            currentXp = tamashiXp,
            healthPercentage = healthPercentage,
            completedObjectives = completedObjectives,
            totalObjectives = totalObjectives,
            onDismiss = { showInfoDialog = false }
        )
    }
}

/**
 * Diálogo que muestra información detallada sobre el sistema del Tamashi
 */
@Composable
private fun TamashiInfoDialog(
    currentLevel: TamashiLevel,
    currentXp: Int,
    healthPercentage: Float,
    completedObjectives: Int,
    totalObjectives: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFEF88B6)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Información del Tamashi")
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Sección de Vida
                Text(
                    text = "❤️ Sistema de Vida",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La vida de tu Tamashi depende de cuántos objetivos tienes completados:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Vida mínima: 50% (ningún objetivo completado)\n" +
                            "• Vida máxima: 100% (todos los objetivos completados)\n" +
                            "• Tu vida actual: ${(healthPercentage * 100).toInt()}% ($completedObjectives/$totalObjectives)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sección de Niveles
                Text(
                    text = "⭐ Sistema de Niveles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF88B6)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gana XP completando objetivos. Tu Tamashi crecerá a medida que sube de nivel:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de niveles
                TamashiLevel.entries.forEach { level ->
                    val isCurrentLevel = level == currentLevel
                    val isUnlocked = currentXp >= level.requiredXp

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCurrentLevel) Color(0xFFEF88B6).copy(alpha = 0.1f)
                                else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Indicador de estado
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = when {
                                        isCurrentLevel -> Color(0xFFEF88B6)
                                        isUnlocked -> Color(0xFF4CAF50)
                                        else -> Color.Gray.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrentLevel) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Text(
                                    text = "${level.levelNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUnlocked) Color.White else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = level.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrentLevel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "${level.requiredXp} XP requerido",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Estado
                        if (isCurrentLevel) {
                            Text(
                                text = "ACTUAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFEF88B6),
                                fontWeight = FontWeight.Bold
                            )
                        } else if (isUnlocked) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    if (level != TamashiLevel.entries.last()) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // XP actual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFEF88B6).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Tu progreso actual",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFEF88B6)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "XP Total: $currentXp",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val nextLevel = TamashiLevel.getNextLevel(currentLevel)
                        if (nextLevel != null) {
                            Text(
                                text = "Siguiente nivel: ${nextLevel.displayName} (${nextLevel.requiredXp - currentXp} XP restantes)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        } else {
                            Text(
                                text = "¡Has alcanzado el nivel máximo! 🎉",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tip
                Text(
                    text = "💡 Tip: Completa objetivos para ganar +1 XP cada uno.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", color = Color(0xFFEF88B6))
            }
        }
    )
}
