package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.oolestudio.tamashi.viewmodel.tutorial.TutorialViewModel
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import com.oolestudio.tamashi.util.tutorial.TutorialLayoutUtils
import kotlinx.coroutines.delay

@Composable
fun TutorialOverlay(
    viewModel: TutorialViewModel,
    modifier: Modifier = Modifier,
    // Callback opcional que se dispara al completar un paso específico
    onStepCompleted: ((stepId: String) -> Unit)? = null
) {
    val ui = viewModel.uiState.collectAsState().value

    // Estados para animación de escritura
    var displayedText by remember(ui.step?.id) { mutableStateOf("") }
    var isAnimating by remember(ui.step?.id) { mutableStateOf(false) }

    val fullText = ui.step?.text ?: ""

    // Lanzamos animación cuando cambia el paso
    LaunchedEffect(ui.step?.id) {
        displayedText = ""
        isAnimating = true
        val chars = fullText.toCharArray()
        for (i in chars.indices) {
            // Si se canceló la animación (por tap), detener el loop
            if (!isAnimating) break
            displayedText += chars[i]
            delay(22) // velocidad de escritura (~45 cps)
        }
        // Al finalizar, marcamos que no está animando
        isAnimating = false
        // Disparamos callback de paso completado (por ejemplo, para navegar)
        ui.step?.id?.let { id -> onStepCompleted?.invoke(id) }
    }

    AnimatedVisibility(visible = ui.visible, enter = fadeIn(), exit = fadeOut()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TutorialLayoutUtils.spacing),
            modifier = modifier.clickable {
                if (isAnimating) {
                    // Saltar animación: mostrar texto completo
                    displayedText = fullText
                    isAnimating = false
                } else {
                    // Avanzar al siguiente paso o cerrar
                    if (ui.step?.nextStepId != null) viewModel.next() else viewModel.dismiss()
                }
            }
        ) {
            ui.step?.let { step ->
                TamashiAvatar(tamashiName = step.tamashiName, assetOverride = step.assetName)
                SpeechBubble(text = displayedText, modifier = Modifier.width(TutorialLayoutUtils.bubbleMaxWidth))
                Spacer(modifier = Modifier.width(TutorialLayoutUtils.spacing))
            }
        }
    }
}

@Composable
fun TamashiMessageOverlay(
    text: String,
    modifier: Modifier = Modifier,
    tamashiName: String = TutorialConfig.tamashiName,
    tamashiAssetName: String = TutorialConfig.tamashiAssetName,
    typewriter: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    var displayedText by remember(text) { mutableStateOf(if (typewriter) "" else text) }
    var isAnimating by remember(text) { mutableStateOf(typewriter) }

    LaunchedEffect(text, typewriter) {
        if (typewriter) {
            displayedText = ""
            val chars = text.toCharArray()
            for (c in chars) {
                if (!isAnimating) break
                displayedText += c
                delay(22)
            }
            isAnimating = false
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(TutorialLayoutUtils.spacing),
        modifier = modifier.clickable {
            if (isAnimating) {
                displayedText = text
                isAnimating = false
            } else {
                onTap?.invoke()
            }
        }
    ) {
        TamashiAvatar(tamashiName = tamashiName, assetOverride = tamashiAssetName)
        SpeechBubble(text = displayedText, modifier = Modifier.width(TutorialLayoutUtils.bubbleMaxWidth))
        Spacer(modifier = Modifier.width(TutorialLayoutUtils.spacing))
    }
}
