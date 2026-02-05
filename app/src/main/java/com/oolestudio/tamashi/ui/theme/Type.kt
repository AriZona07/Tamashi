package com.oolestudio.tamashi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Define los estilos de tipografía para la aplicación, siguiendo las directrices de Material Design.
 *
 * La variable `Typography` es un objeto que contiene estilos de texto predefinidos y semánticos,
 * como `bodyLarge`, `titleLarge`, `labelSmall`, etc.
 *
 * Estos estilos se aplican globalmente cuando se utiliza `TamashiTheme`.
 * Puedes personalizar cualquier estilo de texto que necesites, o añadir nuevos si es necesario.
 */
val Typography = Typography(
    // Estilo por defecto para el cuerpo de texto principal en la aplicación.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Usa la fuente por defecto del sistema.
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    /*
    * EJEMPLO: Puedes descomentar y personalizar otros estilos de texto predefinidos por Material Design.
    * Por ejemplo, para hacer los títulos más grandes y llamativos.
    *
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
