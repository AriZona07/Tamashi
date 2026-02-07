package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import com.oolestudio.tamashi.data.TamashiLevel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.data.TamashiStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
 * @param tamashiPrefsRepository El repositorio que gestiona las preferencias del Tamashi (XP, nivel, etc.)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val playlistRepository: PlaylistRepository,
    private val tamashiPrefsRepository: TamashiPreferencesRepository? = null
) : ViewModel() {

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
     * Un flujo de estado que expone el conteo total de objetivos en la base de datos.
     * Se usa en PetScreen para determinar si el Tamashi ha nacido.
     */
    val totalObjectivesCount: StateFlow<Int> = playlistRepository.getTotalObjectivesCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    /**
     * Un flujo de estado que expone el conteo de objetivos completados.
     * Se usa para calcular la salud del Tamashi.
     */
    val completedObjectivesCount: StateFlow<Int> = playlistRepository.getCompletedObjectivesCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    // Estado para controlar cuando se debe mostrar la animación de eclosión
    private val _shouldShowHatching = MutableStateFlow(false)
    val shouldShowHatching: StateFlow<Boolean> = _shouldShowHatching.asStateFlow()

    // Estado para indicar que el Tamashi ya nació (la animación de eclosión ya se mostró)
    private val _tamashiHasHatched = MutableStateFlow(false)
    val tamashiHasHatched: StateFlow<Boolean> = _tamashiHasHatched.asStateFlow()

    // Estado del XP del Tamashi
    val tamashiXp: StateFlow<Int> = tamashiPrefsRepository?.flowXp()?.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = 0
    ) ?: MutableStateFlow(0)

    // Estado del nivel del Tamashi (calculado a partir del XP)
    val tamashiLevel: StateFlow<TamashiLevel> = tamashiXp.map { xp ->
        TamashiLevel.fromXp(xp)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TamashiLevel.BABY
    )

    // Estado para indicar que el Tamashi subió de nivel (para mostrar animación)
    private val _levelUpEvent = MutableStateFlow<TamashiLevel?>(null)
    val levelUpEvent: StateFlow<TamashiLevel?> = _levelUpEvent.asStateFlow()

    /**
     * Limpia el evento de subida de nivel después de mostrarlo
     */
    fun clearLevelUpEvent() {
        _levelUpEvent.value = null
    }

    /**
     * Dispara la animación de eclosión y redirige al tab de Mascota.
     * Se llama cuando se agrega el primer objetivo.
     */
    fun triggerHatchingAnimation() {
        _shouldShowHatching.value = true
    }

    /**
     * Marca que la animación de eclosión terminó.
     */
    fun onHatchingComplete() {
        _shouldShowHatching.value = false
        _tamashiHasHatched.value = true
    }

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
     * Si es el primer objetivo, dispara la animación de eclosión del Tamashi.
     */
    fun addObjective(name: String, description: String) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                // Verificamos si actualmente no hay objetivos (antes de agregar)
                val currentCount = totalObjectivesCount.value
                val isFirstObjective = currentCount == 0 && !_tamashiHasHatched.value

                // Agregamos el objetivo
                playlistRepository.addObjectiveToPlaylist(playlistId, name, description)

                // Si es el primer objetivo, disparamos la animación de eclosión
                if (isFirstObjective) {
                    triggerHatchingAnimation()
                }
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
     * Si se completa un objetivo, agrega XP al Tamashi.
     * Si se desmarca un objetivo, resta XP.
     */
    fun toggleObjectiveStatus(objective: Objective) {
        _selectedPlaylistId.value?.let { playlistId ->
            viewModelScope.launch {
                val willBeCompleted = !objective.completed
                playlistRepository.updateObjectiveStatus(playlistId, objective.id, willBeCompleted)

                // Actualizar XP del Tamashi
                tamashiPrefsRepository?.let { prefs ->
                    if (willBeCompleted) {
                        // Objetivo completado: agregar XP
                        val leveledUp = prefs.addXp(1)
                        if (leveledUp) {
                            // ¡El Tamashi subió de nivel!
                            _levelUpEvent.value = TamashiLevel.fromXp(tamashiXp.value + 1)
                        }
                    } else {
                        // Objetivo desmarcado: quitar XP
                        prefs.removeXp(1)
                    }
                }
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
