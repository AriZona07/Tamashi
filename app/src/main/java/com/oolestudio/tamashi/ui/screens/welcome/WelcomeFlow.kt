package com.oolestudio.tamashi.ui.screens.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.viewmodel.tamashiselection.TamashiSelectionViewModel
import com.oolestudio.tamashi.viewmodel.tamashiselection.TamashiSelectionViewModelFactory
import com.oolestudio.tamashi.viewmodel.welcome.WelcomeViewModel
import com.oolestudio.tamashi.viewmodel.welcome.WelcomeViewModelFactory
import com.oolestudio.tamashi.viewmodel.welcome.WelcomeScreen

@Composable
fun WelcomeFlow(onWelcomeComplete: () -> Unit) {
    val welcomeViewModel: WelcomeViewModel = viewModel(
        factory = WelcomeViewModelFactory(
            TamashiPreferencesRepository(LocalContext.current)
        )
    )
    val welcomeUiState by welcomeViewModel.uiState.collectAsState()

    when (welcomeUiState.currentScreen) {
        WelcomeScreen.NameForm -> {
            NameFormScreen(
                userName = welcomeUiState.userName,
                onUserNameChange = welcomeViewModel::onUserNameChange,
                onConfirm = welcomeViewModel::onNameConfirmed
            )
        }
        WelcomeScreen.TamashiSelection -> {
            val tamashiSelectionViewModel: TamashiSelectionViewModel = viewModel(
                factory = TamashiSelectionViewModelFactory(
                    TamashiPreferencesRepository(LocalContext.current)
                )
            )
            TamashiSelectionScreen(
                viewModel = tamashiSelectionViewModel,
                onConfirmed = {
                    val selectedTamashi = tamashiSelectionViewModel.uiState.value.selected
                    if (selectedTamashi != null) {
                        welcomeViewModel.onTamashiSelected(selectedTamashi.name)
                    }
                }
            )
        }
        WelcomeScreen.WelcomeMessage -> {
            WelcomeMessageScreen(
                userName = welcomeUiState.userName,
                tamashiName = welcomeUiState.tamashiName,
                onConfirm = {
                    welcomeViewModel.onWelcomeMessageConfirmed()
                    onWelcomeComplete()
                }
            )
        }
    }
}
