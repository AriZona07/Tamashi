package com.oolestudio.tamashi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenUiState(
    val userName: String = "",
    val newUserName: String = ""
)

class ProfileViewModel(private val repository: TamashiPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.flowUserName().collect { userName ->
                _uiState.update { it.copy(userName = userName, newUserName = userName) }
            }
        }
    }

    fun onUserNameChange(newUserName: String) {
        _uiState.update { it.copy(newUserName = newUserName) }
    }

    fun saveUserName() {
        viewModelScope.launch {
            repository.setUserName(_uiState.value.newUserName)
        }
    }
}
