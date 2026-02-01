package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.oolestudio.tamashi.viewmodel.HomeViewModel

// Sealed class para representar las pantallas disponibles en la barra de navegación inferior.
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inicio")
    object Pet : BottomNavItem("pet", Icons.Default.Pets, "Mascota")
    object Calendar : BottomNavItem("calendar", Icons.Default.DateRange, "Calendario")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "Ajustes")
}

/**
 * Pantalla Principal (MainScreen) que contiene la barra de navegación inferior.
 * Actúa como contenedor para las secciones principales de la app.
 */
@Composable
fun MainScreen(homeViewModel: HomeViewModel) {
    // Estado para saber qué pestaña de la barra inferior está seleccionada.
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navItems = listOf(BottomNavItem.Home, BottomNavItem.Pet, BottomNavItem.Calendar, BottomNavItem.Settings)
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentScreen == item,
                        onClick = { currentScreen = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Contenedor que cambia el contenido según la pestaña seleccionada.
        // 'innerPadding' es importante para que el contenido no quede oculto detrás de la barra inferior.
        when (currentScreen) {
            BottomNavItem.Home -> HomeScreen(homeViewModel = homeViewModel, modifier = Modifier.padding(innerPadding))
            BottomNavItem.Pet -> PetScreen(modifier = Modifier.padding(innerPadding))
            BottomNavItem.Calendar -> CalendarScreen(modifier = Modifier.padding(innerPadding))
            BottomNavItem.Settings -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}