package com.oolestudio.tamashi.ui.screens.pet

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.oolestudio.tamashi.data.TamashiLevel

private fun getLevelEmoji(level: TamashiLevel): String {
    return when (level) {
        TamashiLevel.BABY -> "🍼"
        TamashiLevel.CHILD -> "🧒"
        TamashiLevel.YOUNG -> "🌟"
        TamashiLevel.ADULT -> "💪"
        TamashiLevel.MASTER -> "👑"
    }
}

@Composable
fun LevelUpDialog(
    newLevel: TamashiLevel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Tu Tamashi subió de nivel!") },
        text = { Text("Ahora es un ${newLevel.displayName} ${getLevelEmoji(newLevel)}") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("¡Genial!")
            }
        }
    )
}
