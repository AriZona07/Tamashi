package com.oolestudio.tamashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.screens.MainScreen
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import com.oolestudio.tamashi.viewmodel.ThemeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Una implementación falsa de [PlaylistRepository] para usar en previsualizaciones de Compose y pruebas.
 *
 * Esta clase simula el comportamiento de un repositorio de playlists en memoria, lo que permite
 * desacoplar las vistas previas de la capa de datos real (por ejemplo, una base de datos o una API de red).
 */
class FakePlaylistRepository : PlaylistRepository {
    private val playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val objectives = MutableStateFlow<List<Objective>>(emptyList())

    /**
     * Simula la creación de una nueva playlist y la añade a la lista en memoria.
     * El ID de la playlist se genera secuencialmente.
     */
    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist {
        val newPlaylist = Playlist(id = (playlists.value.size + 1).toString(), name = name, category = category, colorHex = colorHex)
        playlists.value += newPlaylist
        return newPlaylist
    }

    /**
     * Devuelve un [Flow] que emite la lista actual de playlists en memoria.
     */
    override fun getUserPlaylists(): Flow<List<Playlist>> = playlists

    /**
     * Simula la eliminación de una playlist por su ID.
     */
    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        playlists.value = playlists.value.filterNot { it.id == playlistId }
        return Result.success(Unit)
    }

    /**
     * Devuelve un [Flow] que emite la lista de objetivos en memoria.
     * En esta implementación falsa, no filtra por `playlistId` y devuelve todos los objetivos.
     */
    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> = objectives

    /**
     * Simula la adición de un nuevo objetivo a la lista en memoria.
     */
    override suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit> {
        val newObjective = Objective(id = (objectives.value.size + 1).toString(), name = name, description = description)
        objectives.value += newObjective
        return Result.success(Unit)
    }

    /**
     * Simula la actualización de los detalles de un objetivo existente.
     */
    override suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit> {
        objectives.value = objectives.value.map {
            if (it.id == objectiveId) it.copy(name = name, description = description) else it
        }
        return Result.success(Unit)
    }

    /**
     * Simula la actualización del estado de completado de un objetivo.
     */
    override suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit> {
        objectives.value = objectives.value.map {
            if (it.id == objectiveId) it.copy(completed = isCompleted) else it
        }
        return Result.success(Unit)
    }

    /**
     * Simula la eliminación de un objetivo de la lista en memoria.
     */
    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        objectives.value = objectives.value.filterNot { it.id == objectiveId }
        return Result.success(Unit)
    }

    /**
     * Devuelve un Flow con el conteo total de objetivos.
     */
    override fun getTotalObjectivesCount(): Flow<Int> {
        return MutableStateFlow(objectives.value.size)
    }

    /**
     * Devuelve un Flow con el conteo de objetivos completados.
     */
    override fun getCompletedObjectivesCount(): Flow<Int> {
        return MutableStateFlow(objectives.value.count { it.completed })
    }
}


/**
 * Previsualización de la aplicación [App] utilizando [FakePlaylistRepository].
 *
 * Esta previsualización permite renderizar el componente `App` en el editor de Android Studio
 * sin necesidad de ejecutar la aplicación completa ni depender de una capa de datos real.
 */
@Composable
@Preview
fun AppPreview() {
    val context = LocalContext.current
    val tamashiPrefsRepository = TamashiPreferencesRepository(context)
    App(
        homeViewModel = HomeViewModel(
            playlistRepository = FakePlaylistRepository(),
            tamashiPrefsRepository = tamashiPrefsRepository
        ),
        themeViewModel = ThemeViewModel(tamashiPrefsRepository)
    )
}

/**
 * El componente raíz de la aplicación Tamashi.
 *
 * @param homeViewModel El ViewModel que gestiona la lógica de la pantalla principal.
 */
@Composable
fun App(homeViewModel: HomeViewModel, themeViewModel: ThemeViewModel) {
    MaterialTheme {
        MainScreen(homeViewModel = homeViewModel, themeViewModel = themeViewModel)
    }
}
