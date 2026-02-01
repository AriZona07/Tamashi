package com.oolestudio.tamashi.data

import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) que define las operaciones disponibles para gestionar las playlists y sus objetivos.
 * Al igual que AuthRepository, esto separa la definición de las operaciones de su implementación real (Firebase).
 */
interface PlaylistRepository {
    // --- Funciones de Playlist ---

    /**
     * Crea una nueva playlist en la base de datos.
     * @return La playlist creada si tuvo éxito, o null si falló.
     */
    suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist?

    /**
     * Obtiene un flujo en tiempo real de las playlists del usuario.
     * @return Un Flow que emite la lista actualizada de playlists cada vez que hay cambios en la base de datos.
     */
    fun getUserPlaylists(): Flow<List<Playlist>>

    /**
     * Elimina una playlist específica.
     */
    suspend fun deletePlaylist(playlistId: String): Result<Unit>

    // --- Funciones de Objetivos ---

    /**
     * Obtiene un flujo en tiempo real de los objetivos (tareas) dentro de una playlist específica.
     */
    fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>>

    /**
     * Agrega un nuevo objetivo a una playlist existente.
     */
    suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit>

    /**
     * Actualiza el nombre y la descripción de un objetivo existente.
     */
    suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit>

    /**
     * Actualiza el estado de completado de un objetivo (marcar como hecho/pendiente).
     */
    suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit>

    /**
     * Elimina un objetivo específico de una playlist.
     */
    suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit>
}