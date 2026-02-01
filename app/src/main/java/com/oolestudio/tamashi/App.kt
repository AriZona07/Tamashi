package com.oolestudio.tamashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import com.oolestudio.tamashi.ui.screens.MainScreen
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// --- Implementación Falsa (Mock) para Previsualización ---
// Simula el comportamiento del almacenamiento de playlists.
class FakePlaylistRepository : PlaylistRepository {
    private val playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val objectives = MutableStateFlow<List<Objective>>(emptyList())

    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        val newPlaylist = Playlist(id = (playlists.value.size + 1).toString(), name = name, category = category, colorHex = colorHex)
        playlists.value = playlists.value + newPlaylist
        return newPlaylist
    }

    override fun getUserPlaylists(): Flow<List<Playlist>> = playlists

    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        playlists.value = playlists.value.filterNot { it.id == playlistId }
        return Result.success(Unit)
    }

    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> = objectives

    override suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit> {
        val newObjective = Objective(id = (objectives.value.size + 1).toString(), name = name, description = description)
        objectives.value = objectives.value + newObjective
        return Result.success(Unit)
    }

    override suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit> {
        objectives.value = objectives.value.map {
            if (it.id == objectiveId) it.copy(name = name, description = description) else it
        }
        return Result.success(Unit)
    }

    override suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit> {
        objectives.value = objectives.value.map {
            if (it.id == objectiveId) it.copy(completed = isCompleted) else it
        }
        return Result.success(Unit)
    }

    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        objectives.value = objectives.value.filterNot { it.id == objectiveId }
        return Result.success(Unit)
    }
}


@Composable
@Preview
fun AppPreview() {
    App(
        homeViewModel = HomeViewModel(FakePlaylistRepository())
    )
}

@Composable
fun App(homeViewModel: HomeViewModel) {
    MaterialTheme {
        // La app ahora muestra directamente la pantalla principal.
        MainScreen(homeViewModel = homeViewModel)
    }
}
