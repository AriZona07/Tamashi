package com.oolestudio.tamashi.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.oolestudio.tamashi.ui.components.settings.SettingsMenu
import com.oolestudio.tamashi.ui.screens.settings.CreditsScreen
import com.oolestudio.tamashi.ui.screens.settings.LanguageScreen
import com.oolestudio.tamashi.ui.screens.settings.NotificationsScreen
import com.oolestudio.tamashi.ui.screens.settings.ProfileScreen
import com.oolestudio.tamashi.ui.screens.settings.ThemeScreen
import com.oolestudio.tamashi.ui.screens.welcome.TamashiWelcomeScreen
import com.oolestudio.tamashi.viewmodel.ThemeViewModel

// Sealed class para gestionar la navegación interna dentro de la pantalla de Ajustes.
private sealed class SettingsScreenNav {
    object Main : SettingsScreenNav()
    object EditProfile : SettingsScreenNav()
    object ChangeTamashi : SettingsScreenNav()
    object Language : SettingsScreenNav()
    object Notifications : SettingsScreenNav()
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

    when (currentSettingsScreen) {
        is SettingsScreenNav.Main -> {
            SettingsMenu(
                onNavigateToEditProfile = { currentSettingsScreen = SettingsScreenNav.EditProfile },
                onNavigateToLanguage = { currentSettingsScreen = SettingsScreenNav.Language },
                onNavigateToNotifications = { currentSettingsScreen = SettingsScreenNav.Notifications },
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
        // Redirige a TamashiWelcomeScreen
        is SettingsScreenNav.ChangeTamashi -> TamashiWelcomeScreen(
            onConfirmed = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        // Redirige a LanguageScreen
        is SettingsScreenNav.Language -> LanguageScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        // Redirige a NotificationsScreen
        is SettingsScreenNav.Notifications -> NotificationsScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
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
