package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.TamashiLevel
import com.oolestudio.tamashi.ui.screens.pet.HealthInfoDialog
import com.oolestudio.tamashi.ui.screens.pet.LevelUpDialog
import com.oolestudio.tamashi.ui.screens.pet.TamashiAnimation
import com.oolestudio.tamashi.ui.screens.pet.TamashiDialogue
import com.oolestudio.tamashi.ui.screens.pet.TamashiStatsCard
import com.oolestudio.tamashi.viewmodel.HomeViewModel

private enum class DialogueState {
    HIDDEN,
    GREETING,
    LEVEL_UP_HINT
}

private fun getLevelMessage(level: TamashiLevel): String {
    return when (level) {
        TamashiLevel.BABY -> "¡Acabo de nacer! Ayúdame a crecer 🌱"
        TamashiLevel.CHILD -> "¡Estoy creciendo! Sigue así 💪"
        TamashiLevel.YOUNG -> "¡Cada vez soy más fuerte! 🌟"
        TamashiLevel.ADULT -> "¡Somos un gran equipo! 🎯"
        TamashiLevel.MASTER -> "¡Juntos somos imparables! 👑"
    }
}

@Composable
fun PetScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val totalObjectives by homeViewModel.totalObjectivesCount.collectAsState()
    val completedObjectives by homeViewModel.completedObjectivesCount.collectAsState()
    val shouldShowHatching by homeViewModel.shouldShowHatching.collectAsState()
    val tamashiHasHatched by homeViewModel.tamashiHasHatched.collectAsState()
    val userName by homeViewModel.userName.collectAsState()
    val tamashiXp by homeViewModel.tamashiXp.collectAsState()
    val tamashiLevel by homeViewModel.tamashiLevel.collectAsState()
    val levelUpEvent by homeViewModel.levelUpEvent.collectAsState()

    var showInfoDialog by remember { mutableStateOf(false) }
    var dialogueState by remember { mutableStateOf(DialogueState.HIDDEN) }

    val nextLevelXp = TamashiLevel.getNextLevel(tamashiLevel)?.requiredXp
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

    val healthPercentage by remember(totalObjectives, completedObjectives) {
        derivedStateOf {
            if (totalObjectives == 0) {
                1f // 100% si no hay objetivos (recién nacido)
            } else {
                val completionRatio = completedObjectives.toFloat() / totalObjectives.toFloat()
                0.5f + (0.5f * completionRatio)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tamashiHasHatched) {
            TamashiStatsCard(
                tamashiLevel = tamashiLevel,
                healthPercentage = healthPercentage,
                xpProgress = xpProgress,
                onInfoClick = { showInfoDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dialogueState != DialogueState.HIDDEN) {
            val dialogueText = when (dialogueState) {
                DialogueState.GREETING -> "Hola $userName, ¿deseas conocer mi historia?"
                DialogueState.LEVEL_UP_HINT -> "Súbeme de nivel para conocer mi historia"
                DialogueState.HIDDEN -> ""
            }
            TamashiDialogue(text = dialogueText, modifier = Modifier.padding(bottom = 16.dp))
        }

        TamashiAnimation(
            shouldShowHatching = shouldShowHatching,
            tamashiHasHatched = tamashiHasHatched,
            totalObjectives = totalObjectives,
            tamashiLevel = tamashiLevel,
            onHatchingComplete = { homeViewModel.onHatchingComplete() },
            onTamashiClick = {
                if (tamashiHasHatched) {
                    dialogueState = when (dialogueState) {
                        DialogueState.HIDDEN -> DialogueState.GREETING
                        DialogueState.GREETING -> DialogueState.LEVEL_UP_HINT
                        DialogueState.LEVEL_UP_HINT -> DialogueState.HIDDEN
                    }
                }
            }
        )

        if (tamashiHasHatched) {
            Text(
                text = getLevelMessage(tamashiLevel),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showInfoDialog) {
            HealthInfoDialog(onDismiss = { showInfoDialog = false })
        }

        levelUpEvent?.let { newLevel ->
            LevelUpDialog(
                newLevel = newLevel,
                onDismiss = { homeViewModel.clearLevelUpEvent() }
            )
        }
    }
}
