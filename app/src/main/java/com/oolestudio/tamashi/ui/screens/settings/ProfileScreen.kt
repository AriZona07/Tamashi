package com.oolestudio.tamashi.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.components.settings.SettingsButton
import com.oolestudio.tamashi.viewmodel.ProfileViewModel
import com.oolestudio.tamashi.viewmodel.ProfileViewModelFactory

/**
 * Pantalla de edición de Perfil.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onChangeTamashi: () -> Unit, // Navega a la pantalla de selección
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            TamashiPreferencesRepository(
                LocalContext.current
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo para cambiar el nombre
        OutlinedTextField(
            value = uiState.newUserName,
            onValueChange = { viewModel.onUserNameChange(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.saveUserName() },
            // Habilitar solo si el nombre ha cambiado
            enabled = uiState.userName != uiState.newUserName
        ) {
            Text("Cambiar Nombre")
        }

        Spacer(modifier = Modifier.height(32.dp))

        SettingsButton(
            text = "Cambiar Tamashi",
            onClick = onChangeTamashi
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
