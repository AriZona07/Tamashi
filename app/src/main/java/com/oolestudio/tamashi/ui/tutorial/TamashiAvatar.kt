package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.oolestudio.tamashi.util.AssetUtils
import com.oolestudio.tamashi.util.tutorial.TutorialLayoutUtils

@Composable
fun TamashiAvatar(
    tamashiName: String,
    assetOverride: String? = null,
    modifier: Modifier = Modifier
) {
    // Usa el assetOverride o el nombre del tamashi para obtener el recurso raw.
    val rawResId = AssetUtils.getTamashiRawRes(assetOverride ?: tamashiName)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawResId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(TutorialLayoutUtils.avatarSize)
    )
}
