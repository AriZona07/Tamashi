package com.oolestudio.tamashi.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class PlaylistRepositoryImpl : PlaylistRepository {

    // Usamos MutableStateFlow para emitir actualizaciones, simulando el comportamiento en tiempo real
    private val _playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())
    private val _objectivesFlowMap = mutableMapOf<String, MutableStateFlow<List<Objective>>>()

    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        val newPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            colorHex = colorHex
        )
        _playlistsFlow.update { it + newPlaylist }
        // También creamos una entrada para sus objetivos
        _objectivesFlowMap[newPlaylist.id] = MutableStateFlow(emptyList())
        return newPlaylist
    }

    override fun getUserPlaylists(): Flow<List<Playlist>> {
        return _playlistsFlow.asStateFlow()
    }

    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        _playlistsFlow.update { playlists -> playlists.filterNot { it.id == playlistId } }
        _objectivesFlowMap.remove(playlistId)
        return Result.success(Unit)
    }

    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> {
        // Devolvemos el flujo existente o creamos uno nuevo si no existe
        return _objectivesFlowMap.getOrPut(playlistId) { MutableStateFlow(emptyList()) }.asStateFlow()
    }

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

    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        val objectiveFlow = _objectivesFlowMap[playlistId]
            ?: return Result.failure(IllegalArgumentException("Playlist con id $playlistId no encontrada"))

        objectiveFlow.update { objectives -> objectives.filterNot { it.id == objectiveId } }
        return Result.success(Unit)
    }
}
