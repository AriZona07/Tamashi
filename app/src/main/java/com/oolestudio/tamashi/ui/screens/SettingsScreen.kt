package com.oolestudio.tamashi.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.components.settings.SettingsMenu
import com.oolestudio.tamashi.ui.screens.settings.CreditsScreen
import com.oolestudio.tamashi.ui.screens.settings.ProfileScreen
import com.oolestudio.tamashi.ui.screens.settings.ThemeScreen
import com.oolestudio.tamashi.ui.screens.welcome.TamashiSelectionScreen
import com.oolestudio.tamashi.viewmodel.tamashiselection.TamashiSelectionViewModel
import com.oolestudio.tamashi.viewmodel.tamashiselection.TamashiSelectionViewModelFactory
import com.oolestudio.tamashi.viewmodel.theme.ThemeViewModel
import kotlinx.coroutines.launch

// Sealed class para gestionar la navegación interna dentro de la pantalla de Ajustes.
private sealed class SettingsScreenNav {
    object Main : SettingsScreenNav()
    object EditProfile : SettingsScreenNav()
    object ChangeTamashi : SettingsScreenNav()
    object Theme : SettingsScreenNav()
    object Credits : SettingsScreenNav()
}

/**
 * Pantalla principal de Ajustes.
 * Gestiona la navegación hacia las diferentes sub-secciones de configuración.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    var currentSettingsScreen by remember { mutableStateOf<SettingsScreenNav>(SettingsScreenNav.Main) }
    val context = LocalContext.current
    val prefsRepo = remember { TamashiPreferencesRepository(context) }
    val scope = rememberCoroutineScope()

    when (currentSettingsScreen) {
        is SettingsScreenNav.Main -> {
            SettingsMenu(
                onNavigateToEditProfile = { currentSettingsScreen = SettingsScreenNav.EditProfile },
                onNavigateToTheme = { currentSettingsScreen = SettingsScreenNav.Theme },
                onNavigateToCredits = { currentSettingsScreen = SettingsScreenNav.Credits },
                modifier = modifier
            )
        }
        // Redirige a ProfileScreen
        is SettingsScreenNav.EditProfile -> ProfileScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            onChangeTamashi = { currentSettingsScreen = SettingsScreenNav.ChangeTamashi },
            modifier = modifier
        )
        // Redirige a TamashiSelectionScreen
        is SettingsScreenNav.ChangeTamashi -> {
            val selectionVm: TamashiSelectionViewModel = viewModel(
                factory = TamashiSelectionViewModelFactory(prefsRepo)
            )
            val uiState by selectionVm.uiState.collectAsState()

            TamashiSelectionScreen(
                viewModel = selectionVm,
                onConfirmed = {
                    uiState.selected?.let {
                        scope.launch {
                            prefsRepo.setTamashi(it)
                        }
                    }
                    currentSettingsScreen = SettingsScreenNav.Main
                },
                modifier = modifier
            )
        }
        // Redirige a ThemeScreen
        is SettingsScreenNav.Theme -> ThemeScreen(
            viewModel = themeViewModel,
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        // Redirige a CreditsScreen
        is SettingsScreenNav.Credits -> CreditsScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
    }
}
