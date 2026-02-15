package com.oolestudio.tamashi.ui.screens.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oolestudio.tamashi.data.TamashiPreferencesRepository
import com.oolestudio.tamashi.viewmodel.welcome.WelcomeViewModel
import com.oolestudio.tamashi.viewmodel.welcome.WelcomeViewModelFactory

@Composable
fun WelcomeScreen(onWelcomeComplete: () -> Unit) {
    val welcomeViewModel: WelcomeViewModel = viewModel(
        factory = WelcomeViewModelFactory(
            TamashiPreferencesRepository(LocalContext.current)
        )
    )
    val welcomeUiState by welcomeViewModel.uiState.collectAsState()
    val isWelcomeCompleted by TamashiPreferencesRepository(LocalContext.current)
        .flowWelcomeCompleted()
        .collectAsState(initial = false)

    if (isWelcomeCompleted) {
        onWelcomeComplete()
    } else {
        WelcomeFlow(onWelcomeComplete = onWelcomeComplete)
    }
}
