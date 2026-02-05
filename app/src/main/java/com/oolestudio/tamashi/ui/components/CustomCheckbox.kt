package com.oolestudio.tamashi.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

/**
 * Un componente de checkbox personalizado que utiliza iconos de Material Design.
 *
 * Se emplea en la lista de objetivos para permitir al usuario marcar una tarea como completada o pendiente.
 * El uso de `IconButton` como contenedor proporciona una zona de pulsación más grande y una respuesta
 * visual al tacto (efecto "ripple").
 *
 * @param checked El estado actual del checkbox (true si está marcado, false si no).
 * @param onCheckedChange Una función lambda que se invoca cuando el usuario pulsa el checkbox.
 */
@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    IconButton(onClick = onCheckedChange) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.CheckBox,
                contentDescription = "Objetivo completado"
            )
        } else {
            Icon(
                imageVector = Icons.Default.CheckBoxOutlineBlank,
                contentDescription = "Objetivo pendiente"
            )
        }
    }
}
