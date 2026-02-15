package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oolestudio.tamashi.data.PlaylistRepository
import com.oolestudio.tamashi.data.TamashiPreferencesRepository

class HomeViewModelFactory(
    private val playlistRepository: PlaylistRepository,
    private val tamashiPrefsRepository: TamashiPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(playlistRepository, tamashiPrefsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
