package com.oolestudio.tamashi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.oolestudio.tamashi.ui.components.petscreen.TamashiDialogue
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
 * Estado del diálogo del Tamashi
 */
private enum class DialogueState {
    HIDDEN,
    GREETING,
    LEVEL_UP_HINT
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
    val userName by homeViewModel.userName.collectAsState()

    // Estados de nivel y XP
    val tamashiXp by homeViewModel.tamashiXp.collectAsState()
    val tamashiLevel by homeViewModel.tamashiLevel.collectAsState()
    val levelUpEvent by homeViewModel.levelUpEvent.collectAsState()

    // Estado para mostrar el diálogo de información
    var showInfoDialog by remember { mutableStateOf(false) }
    var dialogueState by remember { mutableStateOf(DialogueState.HIDDEN) }

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
            healthPercentage >= 0.8f -> MaterialTheme.colorScheme.primary
            healthPercentage >= 0.6f -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.error
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

                        // Botón de información
                        IconButton(onClick = { showInfoDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Información de Salud"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barra de salud
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
                        // Barra de progreso lineal
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
                        // Emoji de estado
                        Text(text = getHealthEmoji(healthPercentage), fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barra de XP
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

        Spacer(modifier = Modifier.height(16.dp))

        // Espacio para el diálogo
        if (dialogueState != DialogueState.HIDDEN) {
            val dialogueText = when (dialogueState) {
                DialogueState.GREETING -> "Hola $userName, ¿deseas conocer mi historia?"
                DialogueState.LEVEL_UP_HINT -> "Súbeme de nivel para conocer mi historia"
                DialogueState.HIDDEN -> ""
            }
            TamashiDialogue(text = dialogueText, modifier = Modifier.padding(bottom = 16.dp))
        }

        // Contenedor de la animación
        Box(
            modifier = Modifier
                .size(animatedTamashiSize.dp) // Tamaño animado
                .clickable { // Mostrar diálogo al tocar
                    if (tamashiState == TamashiState.AXOLOTL) {
                        dialogueState = when (dialogueState) {
                            DialogueState.HIDDEN -> DialogueState.GREETING
                            DialogueState.GREETING -> DialogueState.LEVEL_UP_HINT
                            DialogueState.LEVEL_UP_HINT -> DialogueState.HIDDEN
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Animación del Huevo
            if (showEgg) {
                LottieAnimation(
                    composition = eggComposition,
                    progress = { eggProgress },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Animación del Ajolote
            if (showAxolotl) {
                LottieAnimation(
                    composition = axolotlComposition,
                    progress = { axolotlProgress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Mensaje debajo del Tamashi
        if (tamashiState == TamashiState.AXOLOTL) {
            Text(
                text = getLevelMessage(tamashiLevel),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Diálogo de información (si se activa)
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("¿Qué es la Salud?") },
                text = {
                    Text(
                        "La salud de tu Tamashi refleja cuántos de tus objetivos has completado. " +
                                "¡Mantén tus tareas al día para que esté feliz y saludable!"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Entendido")
                    }
                }
            )
        }

        // Diálogo de subida de nivel
        levelUpEvent?.let { newLevel ->
            AlertDialog(
                onDismissRequest = { homeViewModel.clearLevelUpEvent() },
                title = { Text("¡Tu Tamashi subió de nivel!") },
                text = { Text("Ahora es un ${newLevel.displayName} ${getLevelEmoji(newLevel)}") },
                confirmButton = {
                    TextButton(onClick = { homeViewModel.clearLevelUpEvent() }) {
                        Text("¡Genial!")
                    }
                }
            )
        }
    }
}
