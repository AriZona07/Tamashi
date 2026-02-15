package com.oolestudio.tamashi.ui.screens.pet

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun HealthInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Qué es la Salud?") },
        text = {
            Text(
                "La salud de tu Tamashi refleja cuántos de tus objetivos has completado. " +
                        "¡Mantén tus tareas al día para que esté feliz y saludable!"
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}
