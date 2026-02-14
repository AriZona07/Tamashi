package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.viewmodel.ThemeViewModel

/**
 * Factory para crear instancias de `ThemeViewModel`.
 */
class ThemeViewModelFactory(private val repository: TamashiPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
