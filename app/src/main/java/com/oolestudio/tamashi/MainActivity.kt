package com.oolestudio.tamashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.oolestudio.tamashi.data.PlaylistRepositoryImpl
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.ui.screens.MainScreen
import com.oolestudio.tamashi.ui.screens.TamashiSelectionScreen
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import com.oolestudio.tamashi.viewmodel.TamashiSelectionViewModel

class MainActivity : ComponentActivity() {

    // Instanciamos el repositorio local
    private val playlistRepository by lazy { PlaylistRepositoryImpl() }

    // Creamos el ViewModel para la pantalla principal
    private val homeViewModel by lazy { HomeViewModel(playlistRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsRepo = TamashiPreferencesRepository(applicationContext)
        val selectionVm = TamashiSelectionViewModel(prefsRepo)
        setContent {
            val ui by selectionVm.uiState.collectAsState()

            // Sincroniza configuración global si hay perfil
            ui.selected?.let {
                TutorialConfig.tamashiName = it.name
                TutorialConfig.tamashiAssetName = it.assetName
            }

            if (!ui.isChosen) {
                TamashiSelectionScreen(viewModel = selectionVm, onConfirmed = { /* navegar al MainScreen */ })
            } else {
                MainScreen(homeViewModel)
            }
        }
    }
}
