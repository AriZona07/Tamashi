package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.ui.screens.settings.LanguageScreen
import com.oolestudio.tamashi.ui.screens.settings.NotificationsScreen
import com.oolestudio.tamashi.ui.screens.settings.ProfileScreen
import com.oolestudio.tamashi.ui.screens.settings.ThemeScreen

// Sealed class para gestionar la navegación interna dentro de la pantalla de Ajustes.
private sealed class SettingsScreenNav {
    object Main : SettingsScreenNav()
    object EditProfile : SettingsScreenNav()
    object Language : SettingsScreenNav()
    object Notifications : SettingsScreenNav()
    object Theme : SettingsScreenNav()
}

/**
 * Pantalla principal de Ajustes.
 * Gestiona la navegación hacia las diferentes sub-secciones de configuración.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var currentSettingsScreen by remember { mutableStateOf<SettingsScreenNav>(SettingsScreenNav.Main) }

    when (currentSettingsScreen) {
        is SettingsScreenNav.Main -> {
            SettingsMenuList(
                onNavigateToEditProfile = { currentSettingsScreen = SettingsScreenNav.EditProfile },
                onNavigateToLanguage = { currentSettingsScreen = SettingsScreenNav.Language },
                onNavigateToNotifications = { currentSettingsScreen = SettingsScreenNav.Notifications },
                onNavigateToTheme = { currentSettingsScreen = SettingsScreenNav.Theme },
                modifier = modifier
            )
        }
        is SettingsScreenNav.EditProfile -> ProfileScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        is SettingsScreenNav.Language -> LanguageScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        is SettingsScreenNav.Notifications -> NotificationsScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
        is SettingsScreenNav.Theme -> ThemeScreen(
            onBack = { currentSettingsScreen = SettingsScreenNav.Main },
            modifier = modifier
        )
    }
}

/**
 * Lista de opciones del menú principal de ajustes.
 */
@Composable
private fun SettingsMenuList(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var soundEffectsMuted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Ajustes", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onNavigateToEditProfile, modifier = Modifier.fillMaxWidth()) { Text("Editar Perfil") }
            Button(onClick = onNavigateToLanguage, modifier = Modifier.fillMaxWidth()) { Text("Idioma") }
            Button(onClick = onNavigateToNotifications, modifier = Modifier.fillMaxWidth()) { Text("Notificaciones") }
            Button(onClick = onNavigateToTheme, modifier = Modifier.fillMaxWidth()) { Text("Tema") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Silenciar Efectos de Sonido")
                Switch(checked = soundEffectsMuted, onCheckedChange = { soundEffectsMuted = it })
            }
        }
    }
}