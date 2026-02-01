package com.oolestudio.tamashi.data

/**
 * Representa un único objetivo o tarea dentro de una playlist.
 *
 * @param id El ID único del documento en Firestore.
 * @param name El nombre del objetivo (ej. "Tomar agua").
 * @param description Una descripción opcional para el objetivo.
 * @param completed Si el objetivo ha sido marcado como completado o no.
 */
data class Objective(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val completed: Boolean = false // Renombrado de isCompleted a completed
)