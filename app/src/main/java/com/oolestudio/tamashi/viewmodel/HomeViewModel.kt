package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la aplicación, encargado de la lógica de la pantalla de inicio.
 *
 * Gestiona el estado de la UI, incluyendo la lista de playlists y los objetivos de la playlist
 * seleccionada. Interactúa con el [PlaylistRepository] para realizar operaciones de datos
 * como crear, leer, actualizar y eliminar playlists y objetivos.
 *
 * @param playlistRepository El repositorio que proporciona acceso a los datos de playlists y objetivos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    /**
     * Un flujo de estado que expone la lista de playlists del usuario.
     * Se actualiza automáticamente cuando los datos subyacentes cambian.
     */
    val playlists: StateFlow<List<Playlist>> = playlistRepository.getUserPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // Mantiene la suscripción activa por 5s después de que la UI se va.
            initialValue = emptyList()
        )

    // Flujo interno para mantener el ID de la playlist que el usuario ha seleccionado.
    private val _selectedPlaylistId = MutableStateFlow<String?>(null)
    val selectedPlaylistId: StateFlow<String?> = _selectedPlaylistId.asStateFlow()

    /**
     * Un flujo que expone los objetivos de la playlist actualmente seleccionada.
     *
     * Utiliza `flatMapLatest` para reaccionar a los cambios en `_selectedPlaylistId`. Cuando el usuario
     * selecciona una nueva playlist, cancela la suscripción al flujo de objetivos anterior y se suscribe
     * al nuevo, asegurando que la UI siempre muestre los objetivos correctos.
     */
    val objectives: StateFlow<List<Objective>> = _selectedPlaylistId.flatMapLatest { playlistId ->
        if (playlistId != null) {
            playlistRepository.getObjectivesForPlaylist(playlistId)
        } else {
            emptyFlow() // Si no hay playlist seleccionada, emite una lista vacía.
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    /**
     * Establece la playlist activa para mostrar sus objetivos.
     */
    fun selectPlaylist(playlistId: String?) {
        _selectedPlaylistId.value = playlistId
    }

    /**
     * Lanza una corutina para crear una nueva playlist a través del repositorio.
     */
    fun createPlaylist(name: String, category: String, colorHex: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, category, colorHex)
        }
    }

    /**
     * Lanza una corutina para eliminar una playlist por su ID.
     */
    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    /**
     * Lanza una corutina para añadir un nuevo objetivo a la playlist seleccionada.
     */
    fun addObjective(name: String, description: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.addObjectiveToPlaylist(playlistId, name, description)
            }
        }
    }

    /**
     * Lanza una corutina para actualizar los detalles de un objetivo.
     */
    fun updateObjective(objectiveId: String, name: String, description: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.updateObjective(playlistId, objectiveId, name, description)
            }
        }
    }

    /**
     * Lanza una corutina para cambiar el estado de completado de un objetivo.
     */
    fun toggleObjectiveStatus(objective: Objective) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.updateObjectiveStatus(playlistId, objective.id, !objective.completed)
            }
        }
    }

    /**
     * Lanza una corutina para eliminar un objetivo.
     */
    fun deleteObjective(objectiveId: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                playlistRepository.deleteObjective(playlistId, objectiveId)
            }
        }
    }
}
