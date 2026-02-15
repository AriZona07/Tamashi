package com.oolestudio.tamashi.viewmodel.tamashiselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.data.TamashiProfile
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TamashiSelectionUiState(
    val options: List<TamashiProfile> = listOf(
        TamashiProfile(name = "Bublu", assetName = "ajolote") // Asset actualizado
        // Agrega más Tamashis aquí, por ejemplo:
        // TamashiProfile(name = "Kumo", assetName = "kumo_asset")
    ),
    val selected: TamashiProfile? = null
)

class TamashiSelectionViewModel(
    private val repo: TamashiPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TamashiSelectionUiState())
    val uiState: StateFlow<TamashiSelectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = repo.flowSelectedTamashi().first()
            _uiState.value = _uiState.value.copy(
                selected = profile ?: _uiState.value.options.firstOrNull()
            )
            // Sync a TutorialConfig si hay perfil
            profile?.let {
                TutorialConfig.tamashiName = it.name
                TutorialConfig.tamashiAssetName = it.assetName
            }
        }
    }

    fun selectTamashi(profile: TamashiProfile) {
        _uiState.value = _uiState.value.copy(selected = profile)
    }
}
