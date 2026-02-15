package com.oolestudio.tamashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.data.local.RoomPlaylistRepository
import com.oolestudio.tamashi.data.local.TamashiDatabase
import com.oolestudio.tamashi.ui.screens.MainScreen
import com.oolestudio.tamashi.ui.screens.welcome.WelcomeScreen
import com.oolestudio.tamashi.viewmodel.theme.ThemeViewModel
import com.oolestudio.tamashi.viewmodel.theme.ThemeViewModelFactory
import com.oolestudio.tamashi.ui.theme.TamashiTheme
import com.oolestudio.tamashi.util.tutorial.TutorialConfig
import com.oolestudio.tamashi.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    // Instanciamos la base de datos Room
    private val database by lazy { TamashiDatabase.getDatabase(applicationContext) }

    // Creamos el repositorio usando los DAOs de Room
    private val playlistRepository by lazy {
        RoomPlaylistRepository(
            playlistDao = database.playlistDao(),
            objectiveDao = database.objectiveDao()
        )
    }

    // Repositorio de preferencias del Tamashi
    private val tamashiPrefsRepository by lazy { TamashiPreferencesRepository(applicationContext) }

    // ViewModel para la pantalla de configuración de tema
    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModelFactory(tamashiPrefsRepository)
    }

    // Creamos el ViewModel para la pantalla principal
    private val homeViewModel by lazy { HomeViewModel(playlistRepository, tamashiPrefsRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSetting by themeViewModel.themeSetting.collectAsState()
            var showWelcome by remember { mutableStateOf(true) }
            val isWelcomeCompleted by tamashiPrefsRepository.flowWelcomeCompleted().collectAsState(initial = false)

            if (isWelcomeCompleted) {
                showWelcome = false
            }

            TamashiTheme(themeSetting = themeSetting) {
                if (showWelcome) {
                    WelcomeScreen(onWelcomeComplete = { showWelcome = false })
                } else {
                    val selectedTamashi by tamashiPrefsRepository.flowSelectedTamashi().collectAsState(null)
                    selectedTamashi?.let {
                        TutorialConfig.tamashiName = it.name
                        TutorialConfig.tamashiAssetName = it.assetName
                    }
                    MainScreen(homeViewModel, themeViewModel)
                }
            }
        }
    }
}
