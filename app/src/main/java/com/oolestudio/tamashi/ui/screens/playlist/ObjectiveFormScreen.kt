package com.oolestudio.tamashi.ui.screens.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.Objective

/**
 * Pantalla de Formulario para crear o editar un Objetivo (Tarea).
 * Reutilizable tanto para la creación como para la edición.
 *
 * @param existingObjective Si se pasa un objetivo, el formulario se pre-llena para editar. Si es null, es para crear uno nuevo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectiveFormScreen(
    existingObjective: Objective?, // Si es null, es para crear. Si no, para editar.
    onSave: (name: String, description: String) -> Unit,
    onBack: () -> Unit
) {
    // Estados locales del formulario.
    var name by remember { mutableStateOf(existingObjective?.name ?: "") }
    var description by remember { mutableStateOf(existingObjective?.description ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingObjective == null) "Nuevo Objetivo" else "Editar Objetivo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name, description)
                            } else {
                                error = "El nombre del objetivo no puede estar vacío."
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite scroll si el contenido es largo
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Nombre del objetivo") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null
            )

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 150) description = it },
                label = { Text("Descripción (opcional)") },
                supportingText = { Text("${description.length} / 150") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
