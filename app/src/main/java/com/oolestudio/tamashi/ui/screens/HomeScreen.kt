package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.util.getIconForCategory
import com.oolestudio.tamashi.ui.screens.playlist.CreatePlaylistScreen
import com.oolestudio.tamashi.ui.screens.playlist.PlaylistDetailScreen
import com.oolestudio.tamashi.ui.tutorial.TutorialOverlay
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import com.oolestudio.tamashi.viewmodel.TutorialViewModel
import com.oolestudio.tamashi.data.tutorial.TutorialRepositoryImpl
import com.oolestudio.tamashi.util.tutorial.TutorialConfig

// Capas visuales en Home:
// 1) Botón centrado "Aprender a Agregar una Playlist" cuando no hay playlists.
// 2) FAB "+ Nueva Playlist" sobre la lista cuando hay playlists.
// 3) Overlay de Tutorial por encima, con Tamashi y globo.

// Sealed class para manejar la navegación interna dentro de la pestaña de Inicio (Home).
// Permite cambiar entre la lista de playlists, la creación de una nueva y el detalle de una existente.
private sealed class HomeScreenNav {
    object List : HomeScreenNav() // Muestra la lista de playlists
    object Create : HomeScreenNav() // Muestra el formulario para crear playlist
    data class Detail(val playlist: Playlist) : HomeScreenNav() // Muestra los detalles de una playlist específica
}

/**
 * Pantalla principal de Inicio (Home).
 * Gestiona la navegación entre la lista de playlists y sus sub-pantallas.
 */
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    // Estado para controlar qué sub-pantalla se muestra actualmente.
    var currentScreen by remember { mutableStateOf<HomeScreenNav>(HomeScreenNav.List) }

    // Instancia local del TutorialViewModel (reutilizable por esta pantalla)
    val tutorialViewModel = remember {
        TutorialViewModel(TutorialRepositoryImpl())
    }

    Box(modifier = modifier) {
        // Renderiza la pantalla correspondiente según el estado actual.
        when (currentScreen) {
            is HomeScreenNav.List -> {
                PlaylistListScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToCreate = { currentScreen = HomeScreenNav.Create },
                    onPlaylistSelected = { playlist ->
                        homeViewModel.selectPlaylist(playlist.id)
                        currentScreen = HomeScreenNav.Detail(playlist)
                    },
                    onStartTutorial = {
                        val steps = listOf(
                            com.oolestudio.tamashi.data.tutorial.TutorialStep(
                                id = "step1",
                                tamashiName = TutorialConfig.tamashiName,
                                text = "Para crear una nueva playlist debes utilizar el botón de abajo \"Nueva Playlist\"",
                                assetName = TutorialConfig.tamashiAssetName,
                                nextStepId = "step2"
                            ),
                            com.oolestudio.tamashi.data.tutorial.TutorialStep(
                                id = "step2",
                                tamashiName = TutorialConfig.tamashiName,
                                text = "Listo, ahora ponle un nombre a tu playlist, por ejemplo: \"Ejercicio\", \"Yoga\", o \"Estudiar\"",
                                assetName = TutorialConfig.tamashiAssetName,
                                nextStepId = "step3"
                            ),
                            com.oolestudio.tamashi.data.tutorial.TutorialStep(
                                id = "step3",
                                tamashiName = TutorialConfig.tamashiName,
                                text = "Después selecciona la categoría, por ejemplo, si tu playlist se llama \"Ejercicio\" ponla en \"Salud Física\"",
                                assetName = TutorialConfig.tamashiAssetName,
                                nextStepId = "step4"
                            ),
                            com.oolestudio.tamashi.data.tutorial.TutorialStep(
                                id = "step4",
                                tamashiName = TutorialConfig.tamashiName,
                                text = "Ahora escoge tu color favorito para que tu playlist se pinte de ese color, y dale a \"Crear\" arriba a la derecha",
                                assetName = TutorialConfig.tamashiAssetName,
                                nextStepId = null
                            )
                        )
                        tutorialViewModel.reset()
                        tutorialViewModel.loadTutorial(
                            tutorialId = "home_playlists",
                            steps = steps,
                            startStepId = "step1"
                        )
                    }
                )
            }
            is HomeScreenNav.Create -> {
                CreatePlaylistScreen(
                    onCreatePlaylist = { playlistName, category, colorHex ->
                        // Llamamos al ViewModel para crear la playlist y luego volvemos a la lista.
                        homeViewModel.createPlaylist(playlistName, category, colorHex)
                        currentScreen = HomeScreenNav.List
                    },
                    onBack = { currentScreen = HomeScreenNav.List },
                    tutorialViewModel = tutorialViewModel
                )
            }
            is HomeScreenNav.Detail -> {
                PlaylistDetailScreen(
                    // Gracias al "smart casting" de Kotlin, podemos acceder a `playlist` directamente.
                    playlistName = (currentScreen as HomeScreenNav.Detail).playlist.name,
                    viewModel = homeViewModel,
                    onBack = { currentScreen = HomeScreenNav.List }
                )
            }
        }

        TutorialOverlay(
            viewModel = tutorialViewModel,
            onStepCompleted = { stepId ->
                if (stepId == "step1") {
                    currentScreen = HomeScreenNav.Create
                }
            }
        )
    }
}

/**
 * Componente interno que muestra la lista de playlists y el botón flotante para crear una nueva.
 */
@Composable
private fun PlaylistListScreen(
    homeViewModel: HomeViewModel,
    onNavigateToCreate: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
    // Nuevo: callback para iniciar el tutorial
    onStartTutorial: () -> Unit
) {
    // Observamos el flujo de playlists del ViewModel como un estado de Compose.
    val playlists by homeViewModel.playlists.collectAsState()

    // Estado para controlar qué playlist se va a eliminar (para mostrar el diálogo de confirmación).
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }

    // Si hay una playlist seleccionada para eliminar, mostramos el diálogo de alerta.
    playlistToDelete?.let { playlist ->
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            title = { Text("Eliminar Playlist") },
            text = { Text("¿Estás seguro de que quieres eliminar la playlist '${playlist.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.deletePlaylist(playlist.id)
                        playlistToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Si no hay playlists, mostramos el botón centrado para iniciar el tutorial.
    if (playlists.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onStartTutorial) {
                    Text("Aprender a Agregar una Playlist", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        return
    }

    // Scaffold provee la estructura básica visual (como el botón flotante FAB).
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                icon = { Icon(Icons.Default.Add, contentDescription = "Crear playlist") },
                text = { Text("Nueva Playlist") }
            )
        }
    ) { innerPadding ->
        // LazyColumn es una lista eficiente que solo renderiza los elementos visibles en pantalla.
        LazyColumn(contentPadding = innerPadding) {
            items(playlists) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = { onPlaylistSelected(playlist) },
                    onLongClick = { playlistToDelete = playlist } // Pulsación larga para eliminar
                )
            }
        }
    }
}

/**
 * Componente que representa un elemento individual (una tarjeta) en la lista de playlists.
 */
@OptIn(ExperimentalFoundationApi::class) // Necesario para usar combinedClickable (clicks largos)
@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // Intentamos parsear el color hexadecimal, si falla usamos un color por defecto.
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(playlist.colorHex))
    } catch (_: Exception) { // Se usa `_` para indicar que el parámetro de la excepción no se utiliza.
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForCategory(playlist.category),
                contentDescription = "Categoría: ${playlist.category}",
                modifier = Modifier.size(32.dp),
                tint = if (isSystemInDarkTheme()) Color.Black else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSystemInDarkTheme()) Color.Black else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
