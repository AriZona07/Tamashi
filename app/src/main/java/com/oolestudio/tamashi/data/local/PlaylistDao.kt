package com.oolestudio.tamashi.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones de base de datos relacionadas con Playlists.
 * Room genera automáticamente la implementación de estos métodos.
 */
@Dao
interface PlaylistDao {

    /**
     * Obtiene todas las playlists ordenadas por nombre.
     * @return Un [Flow] que emite la lista de playlists cada vez que hay cambios en la tabla.
     */
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    /**
     * Obtiene una playlist específica por su ID.
     * @param playlistId El ID de la playlist a buscar.
     * @return La playlist encontrada o null si no existe.
     */
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: String): PlaylistEntity?

    /**
     * Inserta una nueva playlist en la base de datos.
     * Si ya existe una playlist con el mismo ID, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    /**
     * Actualiza una playlist existente.
     */
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    /**
     * Elimina una playlist de la base de datos.
     * Los objetivos asociados se eliminan automáticamente (CASCADE).
     */
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: String)
}

