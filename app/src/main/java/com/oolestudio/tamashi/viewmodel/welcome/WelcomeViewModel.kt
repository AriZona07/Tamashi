package com.oolestudio.tamashi.viewmodel.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class WelcomeScreen {
    NameForm,
    TamashiSelection,
    WelcomeMessage
}

data class WelcomeUiState(
    val currentScreen: WelcomeScreen = WelcomeScreen.NameForm,
    val userName: String = "",
    val tamashiName: String = ""
)

class WelcomeViewModel(private val repository: TamashiPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onUserNameChange(newUserName: String) {
        _uiState.update { it.copy(userName = newUserName) }
    }

    fun onNameConfirmed() {
        viewModelScope.launch {
            repository.setUserName(_uiState.value.userName)
            _uiState.update { it.copy(currentScreen = WelcomeScreen.TamashiSelection) }
        }
    }

    fun onTamashiSelected(tamashiName: String) {
        _uiState.update { it.copy(tamashiName = tamashiName, currentScreen = WelcomeScreen.WelcomeMessage) }
    }

    fun onWelcomeMessageConfirmed() {
        viewModelScope.launch {
            repository.setWelcomeCompleted(true)
        }
    }
}
