package com.oolestudio.tamashi.data.local

import com.oolestudio.tamashi.data.Objective
import com.oolestudio.tamashi.data.Playlist
import com.oolestudio.tamashi.data.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementación de [PlaylistRepository] que utiliza Room Database para persistencia.
 *
 * Esta clase actúa como intermediario entre la capa de datos (Room) y la capa de lógica de negocio,
 * convirtiendo las entidades de base de datos a objetos de dominio y viceversa.
 *
 * @param playlistDao El DAO para operaciones con playlists.
 * @param objectiveDao El DAO para operaciones con objetivos.
 */
class RoomPlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val objectiveDao: ObjectiveDao
) : PlaylistRepository {

    /**
     * Crea una nueva playlist y la guarda en la base de datos.
     * @return La playlist creada con su ID generado.
     */
    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist {
        val playlist = Playlist(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            colorHex = colorHex
        )
        playlistDao.insertPlaylist(PlaylistEntity.fromPlaylist(playlist))
        return playlist
    }

    /**
     * Obtiene todas las playlists del usuario como un Flow reactivo.
     * Las entidades se convierten automáticamente a objetos de dominio.
     */
    override fun getUserPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toPlaylist() }
        }
    }

    /**
     * Elimina una playlist por su ID.
     * Los objetivos asociados se eliminan automáticamente gracias a la restricción CASCADE.
     */
    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        return try {
            playlistDao.deletePlaylistById(playlistId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los objetivos de una playlist específica como un Flow reactivo.
     */
    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> {
        return objectiveDao.getObjectivesForPlaylist(playlistId).map { entities ->
            entities.map { it.toObjective() }
        }
    }

    /**
     * Añade un nuevo objetivo a una playlist.
     */
    override suspend fun addObjectiveToPlaylist(
        playlistId: String,
        name: String,
        description: String
    ): Result<Unit> {
        return try {
            val objective = ObjectiveEntity(
                id = UUID.randomUUID().toString(),
                playlistId = playlistId,
                name = name,
                description = description,
                completed = false
            )
            objectiveDao.insertObjective(objective)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el nombre y descripción de un objetivo existente.
     */
    override suspend fun updateObjective(
        playlistId: String,
        objectiveId: String,
        name: String,
        description: String
    ): Result<Unit> {
        return try {
            objectiveDao.updateObjectiveDetails(objectiveId, name, description)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el estado de completado de un objetivo.
     */
    override suspend fun updateObjectiveStatus(
        playlistId: String,
        objectiveId: String,
        isCompleted: Boolean
    ): Result<Unit> {
        return try {
            objectiveDao.updateObjectiveStatus(objectiveId, isCompleted)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un objetivo por su ID.
     */
    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        return try {
            objectiveDao.deleteObjectiveById(objectiveId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el conteo total de objetivos en la base de datos.
     */
    override fun getTotalObjectivesCount(): Flow<Int> {
        return objectiveDao.getTotalObjectivesCount()
    }

    /**
     * Obtiene el conteo de objetivos completados en la base de datos.
     */
    override fun getCompletedObjectivesCount(): Flow<Int> {
        return objectiveDao.getCompletedObjectivesCount()
    }
}

