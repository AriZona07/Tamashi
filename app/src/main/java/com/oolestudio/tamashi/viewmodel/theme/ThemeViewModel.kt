package com.oolestudio.tamashi.viewmodel.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.data.ThemeSetting
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de configuración de tema.
 */
class ThemeViewModel(private val preferencesRepository: TamashiPreferencesRepository) : ViewModel() {

    /**
     * Flujo que emite la configuración de tema actual guardada.
     */
    val themeSetting: StateFlow<ThemeSetting> = preferencesRepository.flowThemeSetting()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = ThemeSetting.SYSTEM
        )

    /**
     * Actualiza la preferencia de tema del usuario.
     */
    fun updateThemeSetting(newSetting: ThemeSetting) {
        viewModelScope.launch {
            preferencesRepository.setThemeSetting(newSetting)
        }
    }
}
