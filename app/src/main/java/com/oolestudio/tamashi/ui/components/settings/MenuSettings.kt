package com.oolestudio.tamashi.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Menú principal de la pantalla de Ajustes.
 */
@Composable
fun SettingsMenu(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        SettingsCard(title = "Cuenta") {
            SettingsButton(text = "Editar Perfil", onClick = onNavigateToEditProfile)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard(title = "Apariencia") {
            SettingsButton(text = "Tema", onClick = onNavigateToTheme)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard(title = "Acerca de") {
            SettingsButton(text = "Créditos", onClick = onNavigateToCredits)
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
