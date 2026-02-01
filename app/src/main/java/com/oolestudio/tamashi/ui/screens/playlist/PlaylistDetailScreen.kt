package com.oolestudio.tamashi.ui.screens.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.ui.components.CustomCheckbox
import com.oolestudio.tamashi.viewmodel.HomeViewModel

// Sealed class para la navegación interna dentro del detalle de la playlist.
// Permite ver la lista de objetivos, añadir uno nuevo o editar uno existente.
private sealed class PlaylistDetailNav {
    object List : PlaylistDetailNav()
    object Add : PlaylistDetailNav()
    data class Edit(val objective: Objective) : PlaylistDetailNav()
}

/**
 * Pantalla de Detalle de Playlist.
 * Muestra la lista de objetivos (tareas) de la playlist seleccionada y permite gestionarlos.
 */
@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<PlaylistDetailNav>(PlaylistDetailNav.List) }

    when (val screen = currentScreen) {
        is PlaylistDetailNav.List -> {
            ObjectiveListScreen(
                playlistName = playlistName,
                viewModel = viewModel,
                onBack = onBack,
                onNavigateToAdd = { currentScreen = PlaylistDetailNav.Add },
                onNavigateToEdit = { objective -> currentScreen = PlaylistDetailNav.Edit(objective) }
            )
        }
        is PlaylistDetailNav.Add -> {
            // Reutilizamos ObjectiveFormScreen para crear.
            ObjectiveFormScreen(
                existingObjective = null,
                onSave = { name, description ->
                    viewModel.addObjective(name, description)
                    currentScreen = PlaylistDetailNav.List
                },
                onBack = { currentScreen = PlaylistDetailNav.List }
            )
        }
        is PlaylistDetailNav.Edit -> {
            // Reutilizamos ObjectiveFormScreen para editar.
            ObjectiveFormScreen(
                existingObjective = screen.objective,
                onSave = { name, description ->
                    viewModel.updateObjective(screen.objective.id, name, description)
                    currentScreen = PlaylistDetailNav.List
                },
                onBack = { currentScreen = PlaylistDetailNav.List }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectiveListScreen(
    playlistName: String,
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Objective) -> Unit
) {
    // Observamos la lista de objetivos en tiempo real.
    val objectives by viewModel.objectives.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistName) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    TextButton(onClick = onNavigateToAdd) {
                        Text("Nuevo Objetivo")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(objectives) { objective ->
                ObjectiveItem(
                    objective = objective,
                    onToggle = { viewModel.toggleObjectiveStatus(objective) },
                    onEdit = { onNavigateToEdit(objective) }
                )
            }
        }
    }
}

@Composable
private fun ObjectiveItem(
    objective: Objective,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    // Estado para controlar si la descripción del objetivo está expandida o colapsada.
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCheckbox(
                checked = objective.completed,
                onCheckedChange = onToggle
            )
            // El nombre se tacha si la tarea está completada.
            Text(
                text = objective.name,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                textDecoration = if (objective.completed) TextDecoration.LineThrough else null,
                color = if (objective.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else LocalContentColor.current
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Editar Objetivo")
            }
            // Solo mostramos el botón de expandir si hay descripción.
            if (objective.description.isNotBlank()) {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        "Expandir/Contraer Descripción"
                    )
                }
            }
        }
        // Animación para mostrar/ocultar la descripción.
        AnimatedVisibility(visible = isExpanded && objective.description.isNotBlank()) {
            Text(
                text = objective.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 48.dp, top = 4.dp, end = 16.dp)
            )
        }
    }
}