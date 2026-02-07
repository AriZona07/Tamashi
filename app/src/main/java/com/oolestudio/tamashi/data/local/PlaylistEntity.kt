package com.oolestudio.tamashi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oolestudio.tamashi.data.Playlist

/**
 * Entidad de Room que representa una Playlist en la base de datos.
 * Cada fila en la tabla "playlists" corresponde a una instancia de esta clase.
 */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val colorHex: String
) {
    /**
     * Convierte esta entidad de base de datos a un objeto de dominio [Playlist].
     */
    fun toPlaylist(): Playlist = Playlist(
        id = id,
        name = name,
        category = category,
        colorHex = colorHex
    )

    companion object {
        /**
         * Crea una [PlaylistEntity] a partir de un objeto de dominio [Playlist].
         */
        fun fromPlaylist(playlist: Playlist): PlaylistEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            category = playlist.category,
            colorHex = playlist.colorHex
        )
    }
}

