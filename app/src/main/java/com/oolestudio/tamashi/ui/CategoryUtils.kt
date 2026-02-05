package com.oolestudio.tamashi.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Este archivo contiene funciones de utilidad relacionadas con las categorías de las playlists.
 */

/**
 * Devuelve un icono de Material Design correspondiente al nombre de una categoría.
 *
 * Esta función mapea un nombre de categoría (como "Salud Física") a un [ImageVector]
 * específico para ser usado en la interfaz de usuario.
 *
 * @param categoryName El nombre de la categoría.
 * @return El [ImageVector] asociado a la categoría. Si la categoría no se reconoce,
 *         devuelve un icono por defecto (un corazón) para asegurar que la UI no falle.
 */
fun getIconForCategory(categoryName: String): ImageVector {
    return when (categoryName) {
        "Salud Física" -> Icons.Default.FitnessCenter
        "Salud Mental" -> Icons.Default.SelfImprovement
        "Académico" -> Icons.Default.School
        else -> Icons.Default.FavoriteBorder // Icono por defecto para categorías no reconocidas
    }
}
