package com.oolestudio.tamashi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.oolestudio.tamashi.data.ThemeSetting

private val LightColorScheme = lightColorScheme(
    background = AppBackground,      // Color de fondo para pantallas y layouts.
    surface = AppBackground,         // Color para superficies de componentes como tarjetas y menús.
    onBackground = OnAppBackground,  // Color del contenido (texto/iconos) que va sobre el `background`.
    onSurface = OnAppBackground,     // Color del contenido que va sobre una `surface`.
)

private val DarkColorScheme = darkColorScheme(
    background = AppBackgroundDark,
    surface = SurfaceDark,
    onBackground = OnAppBackgroundDark,
    onSurface = OnAppBackgroundDark,
)

/**
 * El Composable principal del tema de la aplicación Tamashi.
 *
 * Aplica el tema según la configuración seleccionada por el usuario (claro, oscuro o sistema).
 *
 * @param themeSetting La configuración de tema a aplicar.
 * @param content El contenido de la UI que se renderizará dentro de este tema.
 */
@Composable
fun TamashiTheme(
    themeSetting: ThemeSetting = ThemeSetting.SYSTEM,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeSetting) {
        ThemeSetting.LIGHT -> LightColorScheme
        ThemeSetting.DARK -> DarkColorScheme
        ThemeSetting.SYSTEM -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
