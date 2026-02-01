package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Pantalla de la Mascota (Tamashi).
 * Aquí se mostrará la visualización e interacción con la mascota virtual.
 */
@Composable
fun PetScreen(modifier: Modifier = Modifier) {
    // Column organiza los elementos verticalmente.
    Column(
        modifier = modifier.fillMaxSize(), // Ocupa todo el espacio disponible
        verticalArrangement = Arrangement.Center, // Centra el contenido verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido horizontalmente
    ) {
        Text("Bienvenido a la pantalla de tu Tamashi")
    }
}