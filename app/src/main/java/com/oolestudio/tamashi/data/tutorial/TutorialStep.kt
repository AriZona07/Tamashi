package com.oolestudio.tamashi.data.tutorial

data class TutorialStep(
    val id: String,
    val tamashiName: String = "Bublu",
    val text: String,
    val assetName: String? = null,
    val dismissible: Boolean = true,
    val nextStepId: String? = null
)

data class TutorialState(
    val tutorialId: String,
    val steps: Map<String, TutorialStep>,
    val currentStepId: String?,
    val isVisible: Boolean = true
)
