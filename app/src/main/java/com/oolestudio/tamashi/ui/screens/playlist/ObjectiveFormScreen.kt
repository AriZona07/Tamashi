package com.oolestudio.tamashi.ui.screens.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var showScheduleDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Diálogo temporal para funciones futuras.
    if (showScheduleDialog) {
        ComingSoonDialog(onDismiss = { showScheduleDialog = false })
    }

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
            
            // Selector de repetición (aún no implementado funcionalmente).
            ScheduleSelectorRow(onClick = { showScheduleDialog = true })

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ScheduleSelectorRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Repetir", style = MaterialTheme.typography.titleMedium)
            Text("Nunca", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Seleccionar repetición")
    }
}

@Composable
private fun ComingSoonDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Próximamente") },
        text = { Text("La función para programar playlists y verlas en el calendario estará disponible en futuras versiones.") },
        confirmButton = { Button(onClick = onDismiss) { Text("Aceptar") } }
    )
}