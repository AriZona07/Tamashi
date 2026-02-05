package com.oolestudio.tamashi.data

/**
 * Representa una única "playlist" o lista de tareas.
 *
 * @param id El ID único de la playlist.
 * @param name El nombre que el usuario le da a la playlist.
 * @param category La categoría de la playlist (ej. "Salud física").
 * @param colorHex El código de color hexadecimal para el fondo de la playlist (ej. "#FFFFFF").
 */
data class Playlist(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val colorHex: String = "#FFFFFF" // Blanco por defecto
)
