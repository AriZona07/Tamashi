package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oolestudio.tamashi.data.TamashiPreferencesRepository

class TamashiSelectionViewModelFactory(private val repository: TamashiPreferencesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TamashiSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TamashiSelectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
