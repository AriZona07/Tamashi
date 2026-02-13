package com.oolestudio.tamashi.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oolestudio.tamashi.ui.components.CreditLink
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pantalla que muestra los créditos de la aplicación.
 *
 * Muestra información sobre el equipo de desarrollo y las atribuciones de los assets de terceros.
 * @param onBack Lambda para gestionar la acción de retroceso.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            // Barra superior con título y botón para volver atrás.
            TopAppBar(
                title = { Text("Créditos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Sección de créditos para el equipo responsable.
            Text(
                "Equipo responsable",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Óol Estudio",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ricardo Abrego\n" +
                    "Esperanza Cituk\n" +
                    "Loyo Torres (Ari)\n" +
                    "Gerónimo Aremy",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Agradecimientos especiales
            Text(
                "Agradecimientos especiales",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Maestra Cynthia (Asesora técnica) \n" +
                    "Maestro Josué Fuentes (Asesor metodológico)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de créditos para assets gratuitos utilizados.
            Text(
                "Assets gratuitos",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Uso del nuevo componente simplificado
            CreditLink(
                text = "Ajolote iconos creados por Rohim - Flaticon",
                url = "https://www.flaticon.es/iconos-gratis/ajolote"
            )
            CreditLink(
                text = "Fondo del logotipo por unsplash.com",
                url = "https://unsplash.com/"
            )
            CreditLink(
                text = "Ajolote animado por LottieFiles",
                url = "https://lottiefiles.com/"
            )
        }
    }
}
