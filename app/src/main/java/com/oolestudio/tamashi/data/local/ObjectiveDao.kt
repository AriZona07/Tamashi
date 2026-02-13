package com.oolestudio.tamashi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones de base de datos relacionadas con Objetivos.
 * Room genera automáticamente la implementación de estos métodos.
 */
@Dao
interface ObjectiveDao {

    /**
     * Obtiene todos los objetivos de una playlist específica.
     * @param playlistId El ID de la playlist.
     * @return Un [Flow] que emite la lista de objetivos cada vez que hay cambios.
     */
    @Query("SELECT * FROM objectives WHERE playlistId = :playlistId ORDER BY completed ASC, name ASC")
    fun getObjectivesForPlaylist(playlistId: String): Flow<List<ObjectiveEntity>>

    /**
     * Obtiene un objetivo específico por su ID.
     */
    @Query("SELECT * FROM objectives WHERE id = :objectiveId")
    suspend fun getObjectiveById(objectiveId: String): ObjectiveEntity?

    /**
     * Inserta un nuevo objetivo en la base de datos.
     * Si ya existe uno con el mismo ID, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjective(objective: ObjectiveEntity)

    /**
     * Actualiza un objetivo existente.
     */
    @Update
    suspend fun updateObjective(objective: ObjectiveEntity)

    /**
     * Actualiza el estado de completado de un objetivo.
     */
    @Query("UPDATE objectives SET completed = :isCompleted WHERE id = :objectiveId")
    suspend fun updateObjectiveStatus(objectiveId: String, isCompleted: Boolean)

    /**
     * Actualiza el nombre y descripción de un objetivo.
     */
    @Query("UPDATE objectives SET name = :name, description = :description WHERE id = :objectiveId")
    suspend fun updateObjectiveDetails(objectiveId: String, name: String, description: String)

    /**
     * Elimina un objetivo por su ID.
     */
    @Query("DELETE FROM objectives WHERE id = :objectiveId")
    suspend fun deleteObjectiveById(objectiveId: String)

    /**
     * Elimina todos los objetivos de una playlist.
     */
    @Query("DELETE FROM objectives WHERE playlistId = :playlistId")
    suspend fun deleteAllObjectivesForPlaylist(playlistId: String)

    /**
     * Obtiene el conteo total de objetivos en la base de datos.
     * @return Un [Flow] que emite el número total de objetivos.
     */
    @Query("SELECT COUNT(*) FROM objectives")
    fun getTotalObjectivesCount(): Flow<Int>

    /**
     * Obtiene el conteo de objetivos completados en la base de datos.
     * @return Un [Flow] que emite el número de objetivos completados.
     */
    @Query("SELECT COUNT(*) FROM objectives WHERE completed = 1")
    fun getCompletedObjectivesCount(): Flow<Int>
}

