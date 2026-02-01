package com.oolestudio.tamashi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Definimos una paleta de colores clara (Light Theme) personalizada.
// Material Design 3 usa un sistema de roles de color (background, surface, primary, etc.).
private val LightColorScheme = lightColorScheme(
    background = AppBackground,      // Color de fondo general de la app
    surface = AppBackground,         // Color de superficies (tarjetas, hojas, menús)
    onBackground = OnAppBackground,  // Color del contenido (texto/iconos) sobre el fondo
    onSurface = OnAppBackground,     // Color del contenido sobre superficies
    // Nota: Puedes personalizar otros roles como 'primary', 'secondary', 'error', etc.
    // Si no se definen, MaterialTheme usará los valores predeterminados de color púrpura.
)

/**
 * Composable que envuelve la aplicación para aplicar el tema visual.
 * Todos los componentes dentro de este tema heredarán los colores y tipografías definidos.
 *
 * @param content El contenido de la UI que se renderizará dentro del tema.
 */
@Composable
fun TamashiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}