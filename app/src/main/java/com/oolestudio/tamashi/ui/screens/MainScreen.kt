package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.oolestudio.tamashi.viewmodel.theme.ThemeViewModel
import com.oolestudio.tamashi.viewmodel.HomeViewModel

// Sealed class para representar las pantallas disponibles en la barra de navegación inferior.
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Playlists")
    object Pet : BottomNavItem("pet", Icons.Default.Pets, "Tamashi")
    object History : BottomNavItem("history", Icons.AutoMirrored.Filled.MenuBook, "Historia")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "Ajustes")
}

/**
 * Pantalla Principal (MainScreen) que contiene la barra de navegación inferior.
 * Actúa como contenedor para las secciones principales de la app.
 */
@Composable
fun MainScreen(homeViewModel: HomeViewModel, themeViewModel: ThemeViewModel) {
    // Estado para saber qué pestaña de la barra inferior está seleccionada.
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    // Observa si se debe mostrar la eclosión del Tamashi
    val shouldShowHatching by homeViewModel.shouldShowHatching.collectAsState()

    // Cuando shouldShowHatching cambia a true, navega al tab de Mascota
    LaunchedEffect(shouldShowHatching) {
        if (shouldShowHatching) {
            currentScreen = BottomNavItem.Pet
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navItems = listOf(BottomNavItem.Home, BottomNavItem.Pet, BottomNavItem.History, BottomNavItem.Settings)
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
            BottomNavItem.Pet -> PetScreen(homeViewModel = homeViewModel, modifier = Modifier.padding(innerPadding))
            BottomNavItem.History -> HistoryScreen(modifier = Modifier.padding(innerPadding))
            BottomNavItem.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                themeViewModel = themeViewModel
            )
        }
    }
}
