package com.oolestudio.tamashi.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

/**
 * Un Checkbox personalizado construido con iconos para un control visual total.
 * Se usa en las listas de objetivos para marcar tareas como completadas.
 *
 * @param checked El estado actual (marcado o no).
 * @param onCheckedChange La función lambda que se llama cuando el usuario hace clic en el icono.
 */
@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    // Usamos un IconButton para darle un área de clic más grande y una respuesta visual (ripple effect).
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