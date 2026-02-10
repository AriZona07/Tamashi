package com.oolestudio.tamashi.data

import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) que define las operaciones disponibles para gestionar las playlists y sus objetivos.
 * Esto separa la definición de las operaciones de su implementación.
 */
interface PlaylistRepository {
    // --- Funciones de Playlist ---

    /**
     * Crea una nueva playlist en la fuente de datos.
     * @return La playlist creada si tuvo éxito, o null si falló.
     */
    suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist?

    /**
     * Obtiene un flujo de las playlists del usuario.
     * @return Un Flow que emite la lista actualizada de playlists cada vez que hay cambios.
     */
    fun getUserPlaylists(): Flow<List<Playlist>>

    /**
     * Elimina una playlist específica.
     */
    suspend fun deletePlaylist(playlistId: String): Result<Unit>

    // --- Funciones de Objetivos ---

    /**
     * Obtiene un flujo de los objetivos (tareas) dentro de una playlist específica.
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

    /**
     * Obtiene el conteo total de objetivos en la base de datos.
     * @return Un Flow que emite el número total de objetivos.
     */
    fun getTotalObjectivesCount(): Flow<Int>

    /**
     * Obtiene el conteo de objetivos completados en la base de datos.
     * @return Un Flow que emite el número de objetivos completados.
     */
    fun getCompletedObjectivesCount(): Flow<Int>
}
