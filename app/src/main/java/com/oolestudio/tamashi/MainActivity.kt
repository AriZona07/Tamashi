package com.oolestudio.tamashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oolestudio.tamashi.data.PlaylistRepositoryImpl
import com.oolestudio.tamashi.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    // Instanciamos el repositorio local
    private val playlistRepository by lazy { PlaylistRepositoryImpl() }

    // Creamos el ViewModel para la pantalla principal
    private val homeViewModel by lazy { HomeViewModel(playlistRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            // Pasamos solo el ViewModel de la pantalla principal a nuestra app
            App(homeViewModel)
        }
    }
}
