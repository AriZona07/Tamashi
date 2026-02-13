package com.oolestudio.tamashi.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oolestudio.tamashi.data.Objective

/**
 * Entidad de Room que representa un Objetivo/Tarea en la base de datos.
 * Cada objetivo pertenece a una playlist específica (relación uno a muchos).
 */
@Entity(
    tableName = "objectives",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE // Si se elimina la playlist, se eliminan sus objetivos
        )
    ],
    indices = [Index(value = ["playlistId"])] // Índice para optimizar consultas por playlistId
)
data class ObjectiveEntity(
    @PrimaryKey
    val id: String,
    val playlistId: String,
    val name: String,
    val description: String,
    val completed: Boolean
) {
    /**
     * Convierte esta entidad de base de datos a un objeto de dominio [Objective].
     */
    fun toObjective(): Objective = Objective(
        id = id,
        name = name,
        description = description,
        completed = completed
    )

    companion object {
        /**
         * Crea una [ObjectiveEntity] a partir de un objeto de dominio [Objective] y un playlistId.
         */
        fun fromObjective(objective: Objective, playlistId: String): ObjectiveEntity = ObjectiveEntity(
            id = objective.id,
            playlistId = playlistId,
            name = objective.name,
            description = objective.description,
            completed = objective.completed
        )
    }
}

