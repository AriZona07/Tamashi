package com.oolestudio.tamashi.ui.screens.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.tutorial.TutorialRepositoryImpl
import com.oolestudio.tamashi.data.tutorial.TutorialStep
import com.oolestudio.tamashi.ui.components.CustomCheckbox
import com.oolestudio.tamashi.ui.tutorial.TutorialOverlay
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import com.oolestudio.tamashi.viewmodel.tutorial.TutorialViewModel

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
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<PlaylistDetailNav>(PlaylistDetailNav.List) }
    val tutorialViewModel = remember {
        TutorialViewModel(TutorialRepositoryImpl())
    }

    // El Box ahora solo se usa para el tutorial, sin aplicar el modifier principal aquí.
    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is PlaylistDetailNav.List -> {
                ObjectiveListScreen(
                    playlistName = playlistName,
                    viewModel = viewModel,
                    onBack = onBack,
                    onNavigateToAdd = { currentScreen = PlaylistDetailNav.Add },
                    onNavigateToEdit = { objective -> currentScreen = PlaylistDetailNav.Edit(objective) },
                    tutorialViewModel = tutorialViewModel,
                    modifier = modifier // Pasamos el modifier al ObjectiveListScreen
                )
            }
            is PlaylistDetailNav.Add -> {
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

        TutorialOverlay(viewModel = tutorialViewModel, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectiveListScreen(
    playlistName: String,
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Objective) -> Unit,
    tutorialViewModel: TutorialViewModel,
    modifier: Modifier = Modifier
) {
    val objectives by viewModel.objectives.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier, // Aplicamos el modifier aquí para que el Scaffold se posicione correctamente.
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
        if (objectives.isEmpty()) {
            EmptyObjectivesScreen(
                modifier = Modifier.padding(innerPadding), // El padding del Scaffold se aplica al contenido.
                onStartTutorial = {
                    val steps = listOf(
                        TutorialStep(
                            id = "step1",
                            tamashiName = TutorialConfig.tamashiName,
                            text = "¡Esta playlist está vacía! Vamos a añadir nuestro primer objetivo.",
                            assetName = TutorialConfig.tamashiAssetName,
                            nextStepId = "step2"
                        ),
                        TutorialStep(
                            id = "step2",
                            tamashiName = TutorialConfig.tamashiName,
                            text = "Usa el botón \"Nuevo Objetivo\" para empezar.",
                            assetName = TutorialConfig.tamashiAssetName,
                            nextStepId = null
                        )
                    )
                    tutorialViewModel.reset()
                    tutorialViewModel.loadTutorial(
                        tutorialId = "objectives_tutorial",
                        steps = steps,
                        startStepId = "step1"
                    )
                },
                viewModel = viewModel
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding), // El padding del Scaffold se aplica al contenido.
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
}


@Composable
private fun EmptyObjectivesScreen(
    modifier: Modifier = Modifier,
    onStartTutorial: () -> Unit,
    viewModel: HomeViewModel
) {
    val hasSeenTutorial by viewModel.hasSeenObjectiveTutorial.collectAsState()

    LaunchedEffect(Unit) {
        if (!hasSeenTutorial) {
            onStartTutorial()
            viewModel.setHasSeenObjectiveTutorial(true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay objetivos en esta playlist.")
            Button(onClick = onStartTutorial) {
                Text("Aprender a crear un objetivo")
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
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
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
