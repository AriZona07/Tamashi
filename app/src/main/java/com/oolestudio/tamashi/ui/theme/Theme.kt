package com.oolestudio.tamashi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Define el esquema de color claro y personalizado para la aplicación.
 *
 * Material Design 3 se basa en un sistema de "roles de color" semánticos (como `background`, `surface`, `primary`)
 * que se asignan a los colores definidos en `Color.kt`. Esto permite que los componentes de Material
 * se coloreen de manera consistente y predecible.
 */
private val LightColorScheme = lightColorScheme(
    background = AppBackground,      // Color de fondo para pantallas y layouts.
    surface = AppBackground,         // Color para superficies de componentes como tarjetas y menús.
    onBackground = OnAppBackground,  // Color del contenido (texto/iconos) que va sobre el `background`.
    onSurface = OnAppBackground,     // Color del contenido que va sobre una `surface`.

    // Se pueden personalizar otros colores (primary, secondary, error, etc.)
    // Si no se especifican, MaterialTheme utilizará los valores por defecto del tema base.
)

/**
 * El Composable principal del tema de la aplicación Tamashi.
 *
 * Envuelve toda la interfaz de usuario de la aplicación, aplicando la paleta de colores (`LightColorScheme`)
 * y los estilos de tipografía definidos en `Type.kt` a todos los componentes descendientes.
 *
 * @param content El contenido de la UI que se renderizará dentro de este tema.
 */
@Composable
fun TamashiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography, // Aplica la tipografía definida en Type.kt
        content = content
    )
}
