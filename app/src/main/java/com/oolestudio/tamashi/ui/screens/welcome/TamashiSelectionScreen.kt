package com.oolestudio.tamashi.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.ui.tutorial.SpeechBubble
import com.oolestudio.tamashi.ui.tutorial.TamashiAvatar
import com.oolestudio.tamashi.viewmodel.tamashiselection.TamashiSelectionViewModel

@Composable
fun TamashiSelectionScreen(
    viewModel: TamashiSelectionViewModel,
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center, // Centrar verticalmente todo el contenido
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeechBubble(text = "Elige tu Tamashi")

        // Pirámide de 3 círculos
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Punta del triángulo: Bublu (clickeable)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isSelected = ui.selected?.name == (ui.options.firstOrNull()?.name ?: "Bublu")
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (isSelected) 4.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.selectTamashi(ui.options.first()) },
                    contentAlignment = Alignment.Center
                ) {
                    TamashiAvatar(
                        tamashiName = ui.options.firstOrNull()?.name ?: "Bublu",
                        assetOverride = ui.options.firstOrNull()?.assetName ?: "ajolote", // Corregido
                        modifier = Modifier.size(120.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = ui.options.firstOrNull()?.name ?: "Bublu", style = MaterialTheme.typography.titleMedium)
            }

            // Base del triángulo: 2 bloqueados (no clickeables)
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                LockedTamashiCircle(label = "Próximamente...")
                LockedTamashiCircle(label = "Próximamente...")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onConfirmed, enabled = ui.selected != null) {
            Text("Confirmar")
        }
    }
}

@Composable
private fun LockedTamashiCircle(label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "Bloqueado", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}
