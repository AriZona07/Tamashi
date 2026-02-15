package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.tutorial.TutorialRepository
import com.oolestudio.tamashi.data.tutorial.TutorialState
import com.oolestudio.tamashi.data.tutorial.TutorialStep
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Evitar colisión de nombre con otras UI state del proyecto
data class TutorialOverlayUiState(
    val visible: Boolean = false,
    val step: TutorialStep? = null
)

class TutorialViewModel(
    private val repo: TutorialRepository
) : ViewModel() {

    val uiState: StateFlow<TutorialOverlayUiState> =
        repo.state.map { s: TutorialState? ->
            val step = s?.currentStepId?.let { id -> s.steps[id] }
            TutorialOverlayUiState(visible = s?.isVisible == true && step != null, step = step)
        }.stateIn(viewModelScope, SharingStarted.Lazily, TutorialOverlayUiState())

    // Carga un tutorial por id con sus pasos
    fun loadTutorial(tutorialId: String, steps: List<TutorialStep>, startStepId: String? = steps.firstOrNull()?.id) {
        repo.loadTutorial(tutorialId, steps, startStepId)
    }

    fun next() = repo.next()
    fun dismiss() = repo.dismiss()
    fun reset() = repo.reset()
}
