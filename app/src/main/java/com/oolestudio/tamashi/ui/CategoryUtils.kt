package com.oolestudio.tamashi.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Función de utilidad para obtener iconos basados en categorías.
 *
 * @param categoryName El nombre de la categoría (ej. "Salud Física").
 * @return El `ImageVector` del icono correspondiente de Material Design.
 *         Devuelve un icono por defecto (corazón) si la categoría no coincide con ninguna conocida.
 */
@Composable
fun getIconForCategory(categoryName: String): ImageVector {
    return when (categoryName) {
        "Salud Física" -> Icons.Default.FitnessCenter
        "Salud Mental" -> Icons.Default.SelfImprovement
        "Académico" -> Icons.Default.School
        else -> Icons.Default.FavoriteBorder // Icono por defecto (fallback)
    }
}