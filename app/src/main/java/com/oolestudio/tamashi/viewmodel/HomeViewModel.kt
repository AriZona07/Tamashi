package com.oolestudio.tamashi.viewmodel

import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel principal para la pantalla de Inicio (Home).
 * Gestiona la lista de Playlists y los Objetivos (tareas) asociados a la playlist seleccionada.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val playlistRepository: PlaylistRepository) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // Flujo observable de todas las playlists del usuario.
    val playlists: StateFlow<List<Playlist>> = playlistRepository.getUserPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado interno para saber qué playlist está seleccionada actualmente.
    private val _selectedPlaylistId = MutableStateFlow<String?>(null)

    // Flujo dinámico de objetivos.
    // flatMapLatest observa _selectedPlaylistId. Cada vez que cambia el ID seleccionado,
    // cancela la suscripción anterior y se suscribe al flujo de objetivos de la nueva playlist.
    val objectives: Flow<List<Objective>> = _selectedPlaylistId.flatMapLatest { playlistId ->
        if (playlistId == null) emptyFlow() else playlistRepository.getObjectivesForPlaylist(playlistId)
    }

    /**
     * Selecciona una playlist para ver sus objetivos.
     */
    fun selectPlaylist(playlistId: String) {
        _selectedPlaylistId.value = playlistId
    }

    suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        return playlistRepository.createPlaylist(name, category, colorHex)
    }

    /**
     * Elimina la playlist actualmente seleccionada.
     */
    fun deletePlaylist(onSuccess: () -> Unit) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                val result = playlistRepository.deletePlaylist(playlistId)
                if (result.isSuccess) {
                    onSuccess()
                }
            }
        }
    }

    /**
     * Elimina una playlist específica por su ID (usado desde la lista principal).
     */
    fun deletePlaylistById(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun addObjective(name: String, description: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.addObjectiveToPlaylist(playlistId, name, description)
            }
        }
    }

    fun updateObjective(objectiveId: String, name: String, description: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.updateObjective(playlistId, objectiveId, name, description)
            }
        }
    }

    fun deleteObjective(objectiveId: String, onSuccess: () -> Unit) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                val result = playlistRepository.deleteObjective(playlistId, objectiveId)
                if (result.isSuccess) {
                    onSuccess()
                }
            }
        }
    }

    /**
     * Cambia el estado de completado de un objetivo (check/uncheck).
     */
    fun toggleObjectiveStatus(objective: Objective) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.updateObjectiveStatus(playlistId, objective.id, !objective.completed)
            }
        }
    }
}