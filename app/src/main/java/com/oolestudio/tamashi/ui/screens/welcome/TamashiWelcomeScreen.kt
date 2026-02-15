package com.oolestudio.tamashi.ui.screens.welcome

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.screens.TamashiSelectionScreen
import com.oolestudio.tamashi.viewmodel.TamashiSelectionViewModel
import com.oolestudio.tamashi.viewmodel.TamashiSelectionViewModelFactory

/**
 * Pantalla para seleccionar el Tamashi.
 * Puede ser parte del flujo de bienvenida o para cambiarlo desde el perfil.
 */
@Composable
fun TamashiWelcomeScreen(onConfirmed: () -> Unit, modifier: Modifier = Modifier) {
    val prefsRepo = TamashiPreferencesRepository(context = LocalContext.current)
    val selectionVm: TamashiSelectionViewModel = viewModel(
        factory = TamashiSelectionViewModelFactory(prefsRepo)
    )
    val ui by selectionVm.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Renderiza la selección de Tamashi
        TamashiSelectionScreen(viewModel = selectionVm, onConfirmed = { onConfirmed() })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onConfirmed) {
            Text("Confirmar")
        }
    }
}
