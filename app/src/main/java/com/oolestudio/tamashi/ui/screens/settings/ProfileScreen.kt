package com.oolestudio.tamashi.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.screens.TamashiSelectionScreen
import com.oolestudio.tamashi.viewmodel.TamashiSelectionViewModel

/**
 * Pantalla de edición de Perfil.
 * Incluye la selección de Tamashi (puede cambiarse desde aquí).
 */
@Composable
fun ProfileScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val prefsRepo = TamashiPreferencesRepository(context = androidx.compose.ui.platform.LocalContext.current)
    val selectionVm = TamashiSelectionViewModel(prefsRepo)
    val ui by selectionVm.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Renderiza la selección de Tamashi
        TamashiSelectionScreen(viewModel = selectionVm, onConfirmed = { onBack() })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
