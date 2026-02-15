package com.oolestudio.tamashi.ui.screens.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.ui.theme.PlaylistColors
import com.oolestudio.tamashi.viewmodel.TutorialViewModel

// Modelo de datos interno para las categorías disponibles en la UI.
private data class Category(val name: String, val icon: ImageVector)

// Lista predefinida de categorías.
private val categories = listOf(
    Category("Salud Física", Icons.Default.FitnessCenter),
    Category("Salud Mental", Icons.Default.SelfImprovement),
    Category("Académico", Icons.Default.School)
)

/**
 * Pantalla para crear una nueva Playlist.
 * Permite al usuario elegir nombre, categoría y color.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    onCreatePlaylist: (name: String, category: String, colorHex: String) -> Unit,
    onBack: () -> Unit,
    tutorialViewModel: TutorialViewModel? = null
) {
    // Estados del formulario.
    var playlistName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedColor by remember { mutableStateOf(PlaylistColors.colorOptions.first()) } // Color por defecto: Blanco
    var error by remember { mutableStateOf<String?>(null) }

    // Tutorial: paso actual para decidir resaltados
    val tutorialStepId = tutorialViewModel?.uiState?.collectAsState()?.value?.step?.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva playlist") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    TextButton(
                        onClick = {
                            // Validación simple antes de crear.
                            if (playlistName.isBlank()) {
                                error = "El nombre no puede estar vacío."
                            } else if (selectedCategory == null) {
                                error = "Debes seleccionar una categoría."
                            } else {
                                // Convertimos el color de Compose a Hex String para guardarlo en la BD.
                                val colorHex = "#${Integer.toHexString(selectedColor.toArgb()).uppercase().substring(2)}"
                                onCreatePlaylist(playlistName, selectedCategory!!.name, colorHex)
                            }
                        }
                    ) {
                        Text("Crear")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Nombre: resaltar en step2
            val nameHighlight = tutorialStepId == "step2"
            Column(
                modifier = if (nameHighlight) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)).padding(8.dp) else Modifier
            ) {
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it; error = null },
                    label = { Text("Nombre de la playlist") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null && playlistName.isBlank()
                )
            }

            // Categoría: resaltar en step3
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it; error = null },
                isError = error != null && selectedCategory == null,
                highlight = tutorialStepId == "step3"
            )

            // Color: resaltar en step4
            ColorSelector(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
                highlight = tutorialStepId == "step4"
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun CategorySelector(selectedCategory: Category?, onCategorySelected: (Category) -> Unit, isError: Boolean, highlight: Boolean = false) {
    Column(
        modifier = if (highlight) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)).padding(8.dp) else Modifier
    ) {
        Text("Selecciona una categoría", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            categories.forEach { category ->
                val borderColor = if (isError) MaterialTheme.colorScheme.error else if (selectedCategory == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                CategoryItem(
                    category = category,
                    isSelected = selectedCategory == category,
                    borderColor = borderColor,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(category: Category, isSelected: Boolean, borderColor: Color, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Icon(category.icon, contentDescription = category.name, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ColorSelector(selectedColor: Color, onColorSelected: (Color) -> Unit, highlight: Boolean = false) {
    Column(
        modifier = if (highlight) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)).padding(8.dp) else Modifier
    ) {
        Text("Elige un color", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlaylistColors.colorOptions.forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = "Seleccionado", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
