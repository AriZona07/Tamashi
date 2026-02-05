package com.oolestudio.tamashi.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Implementación concreta de [PlaylistRepository] que simula una base de datos local en memoria.
 *
 * Esta clase gestiona las playlists y sus objetivos utilizando `MutableStateFlow` para
 * mantener el estado y emitir actualizaciones, imitando el comportamiento reactivo que tendría
 * una base de datos como Room o Firestore sin necesidad de una dependencia externa.
 */
class PlaylistRepositoryImpl : PlaylistRepository {

    // Un flujo que contiene la lista de todas las playlists. Emite una nueva lista en cada actualización.
    private val _playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())

    // Un mapa que asocia el ID de una playlist con un flujo que contiene la lista de sus objetivos.
    // Esto permite gestionar los objetivos de cada playlist de forma independiente.
    private val _objectivesFlowMap = mutableMapOf<String, MutableStateFlow<List<Objective>>>()

    /**
     * Crea una nueva playlist, le asigna un ID único y la añade a la lista en memoria.
     */
    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        val newPlaylist = Playlist(
            id = UUID.randomUUID().toString(), // Genera un ID único para la nueva playlist.
            name = name,
            category = category,
            colorHex = colorHex
        )
        _playlistsFlow.update { it + newPlaylist } // Añade la nueva playlist a la lista.
        // Prepara un flujo vacío para los futuros objetivos de esta playlist.
        _objectivesFlowMap[newPlaylist.id] = MutableStateFlow(emptyList())
        return newPlaylist
    }

    /**
     * Devuelve un flujo que emite la lista completa de playlists cada vez que cambia.
     */
    override fun getUserPlaylists(): Flow<List<Playlist>> {
        return _playlistsFlow.asStateFlow()
    }

    /**
     * Elimina una playlist de la lista en memoria y también elimina sus objetivos asociados.
     */
    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        _playlistsFlow.update { playlists -> playlists.filterNot { it.id == playlistId } }
        _objectivesFlowMap.remove(playlistId) // Limpia los objetivos de la playlist eliminada.
        return Result.success(Unit)
    }

    /**
     * Devuelve un flujo que emite la lista de objetivos para una playlist específica.
     * Si no existe un flujo para esa playlist, crea uno nuevo y lo devuelve.
     */
    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> {
        return _objectivesFlowMap.getOrPut(playlistId) { MutableStateFlow(emptyList()) }.asStateFlow()
    }

    /**
     * Añade un nuevo objetivo a una playlist específica.
     */
    override suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit> {
        val objectiveFlow = _objectivesFlowMap[playlistId]
            ?: return Result.failure(IllegalArgumentException("Playlist con id $playlistId no encontrada"))

        val newObjective = Objective(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            completed = false
        )
        objectiveFlow.update { it + newObjective }
        return Result.success(Unit)
    }

    /**
     * Actualiza el nombre y la descripción de un objetivo existente.
     */
    override suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit> {
        val objectiveFlow = _objectivesFlowMap[playlistId]
            ?: return Result.failure(IllegalArgumentException("Playlist con id $playlistId no encontrada"))

        objectiveFlow.update { objectives ->
            objectives.map {
                if (it.id == objectiveId) {
                    it.copy(name = name, description = description)
                } else {
                    it
                }
            }
        }
        return Result.success(Unit)
    }

    /**
     * Actualiza el estado de completado (marcado/desmarcado) de un objetivo.
     */
    override suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit> {
        val objectiveFlow = _objectivesFlowMap[playlistId]
            ?: return Result.failure(IllegalArgumentException("Playlist con id $playlistId no encontrada"))

        objectiveFlow.update { objectives ->
            objectives.map {
                if (it.id == objectiveId) {
                    it.copy(completed = isCompleted)
                } else {
                    it
                }
            }
        }
        return Result.success(Unit)
    }

    /**
     * Elimina un objetivo de la lista de una playlist específica.
     */
    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        val objectiveFlow = _objectivesFlowMap[playlistId]
            ?: return Result.failure(IllegalArgumentException("Playlist con id $playlistId no encontrada"))

        objectiveFlow.update { objectives -> objectives.filterNot { it.id == objectiveId } }
        return Result.success(Unit)
    }
}
