package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import com.oolestudio.tamashi.util.tutorial.TutorialLayoutUtils
import com.oolestudio.tamashi.viewmodel.tutorial.TutorialViewModel
import kotlinx.coroutines.delay

/**
 * TutorialOverlay: capa de tutorial
 * - Posiciona Tamashi en esquina inferior derecha.
 * - Muestra globo encima del Tamashi creciendo hacia arriba.
 * - Avance sólo por tap (no automático al terminar la animación).
 */
@Composable
fun TutorialOverlay(
    viewModel: TutorialViewModel,
    modifier: Modifier = Modifier,
    // Callback opcional que se dispara al tocar cuando el paso actual terminó de animarse (por ejemplo, navegar)
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
        // Ya no disparamos onStepCompleted aquí; el avance ocurre solo con tap
    }

    AnimatedVisibility(visible = ui.visible, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Avatar en esquina inferior derecha
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                Column(horizontalAlignment = Alignment.End) {
                    // Globo encima del Tamashi, creciendo hacia arriba
                    SpeechBubble(
                        text = displayedText,
                        modifier = Modifier
                            .width(TutorialLayoutUtils.bubbleMaxWidth)
                            .padding(bottom = 8.dp),
                        showTail = true,
                        tailSizeDp = 12f,
                        tailOffsetRightDp = 24f
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(TutorialLayoutUtils.spacing),
                        modifier = modifier.clickable {
                            if (isAnimating) {
                                // Saltar animación: mostrar texto completo
                                displayedText = fullText
                                isAnimating = false
                            } else {
                                // Al tocar (y ya sin animación), opcionalmente disparamos callback y avanzamos
                                ui.step?.id?.let { id -> onStepCompleted?.invoke(id) }
                                if (ui.step?.nextStepId != null) viewModel.next() else viewModel.dismiss()
                            }
                        }
                    ) {
                        TamashiAvatar(tamashiName = ui.step?.tamashiName ?: TutorialConfig.tamashiName, assetOverride = ui.step?.assetName)
                    }
                }
            }
        }
    }
}

/**
 * TamashiMessageOverlay: overlay genérico para mensajes no tutoriales.
 * Se suprime el warning de inspección si no se usa aún.
 */
@Suppress("unused")
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
