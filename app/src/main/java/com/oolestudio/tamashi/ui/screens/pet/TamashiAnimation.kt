package com.oolestudio.tamashi.ui.screens.pet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.oolestudio.tamashi.R
import com.oolestudio.tamashi.data.TamashiLevel

internal enum class TamashiState {
    EGG_IDLE,
    EGG_HATCHING,
    AXOLOTL
}

@Composable
fun TamashiAnimation(
    shouldShowHatching: Boolean,
    tamashiHasHatched: Boolean,
    totalObjectives: Int,
    tamashiLevel: TamashiLevel,
    onHatchingComplete: () -> Unit,
    onTamashiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tamashiState by remember(shouldShowHatching, tamashiHasHatched, totalObjectives) {
        derivedStateOf {
            when {
                shouldShowHatching -> TamashiState.EGG_HATCHING
                tamashiHasHatched || totalObjectives > 0 -> TamashiState.AXOLOTL
                else -> TamashiState.EGG_IDLE
            }
        }
    }

    val tamashiSize by remember(tamashiLevel) {
        derivedStateOf {
            when (tamashiLevel) {
                TamashiLevel.BABY -> 150f
                TamashiLevel.CHILD -> 175f
                TamashiLevel.YOUNG -> 200f
                TamashiLevel.ADULT -> 225f
                TamashiLevel.MASTER -> 250f
            }
        }
    }

    val animatedTamashiSize by animateFloatAsState(
        targetValue = tamashiSize,
        animationSpec = tween(durationMillis = 800),
        label = "tamashiSize"
    )

    val eggComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.egg))
    val axolotlComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ajolote))

    val eggClipSpec = when (tamashiState) {
        TamashiState.EGG_IDLE -> LottieClipSpec.Progress(0f, 0.33f)
        else -> null
    }

    val eggProgress by animateLottieCompositionAsState(
        composition = eggComposition,
        iterations = if (tamashiState == TamashiState.EGG_HATCHING) 1 else LottieConstants.IterateForever,
        clipSpec = eggClipSpec,
        isPlaying = tamashiState != TamashiState.AXOLOTL
    )

    LaunchedEffect(eggProgress, tamashiState) {
        if (tamashiState == TamashiState.EGG_HATCHING && eggProgress >= 0.99f) {
            onHatchingComplete()
        }
    }

    val axolotlProgress by animateLottieCompositionAsState(
        composition = axolotlComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = tamashiState == TamashiState.AXOLOTL
    )

    Box(
        modifier = modifier
            .size(animatedTamashiSize.dp)
            .clickable(onClick = onTamashiClick),
        contentAlignment = Alignment.Center
    ) {
        if (tamashiState != TamashiState.AXOLOTL) {
            LottieAnimation(
                composition = eggComposition,
                progress = { eggProgress },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (tamashiState == TamashiState.AXOLOTL) {
            LottieAnimation(
                composition = axolotlComposition,
                progress = { axolotlProgress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
