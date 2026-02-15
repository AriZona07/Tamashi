package com.oolestudio.tamashi.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.R
import com.oolestudio.tamashi.data.ThemeSetting
import com.oolestudio.tamashi.viewmodel.theme.ThemeViewModel

/**
 * Pantalla para seleccionar el tema de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSetting by viewModel.themeSetting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_theme_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            ThemeChoiceRow(
                text = stringResource(R.string.settings_theme_system),
                selected = currentSetting == ThemeSetting.SYSTEM,
                onClick = { viewModel.updateThemeSetting(ThemeSetting.SYSTEM) }
            )

            ThemeChoiceRow(
                text = stringResource(R.string.settings_theme_light),
                selected = currentSetting == ThemeSetting.LIGHT,
                onClick = { viewModel.updateThemeSetting(ThemeSetting.LIGHT) }
            )

            ThemeChoiceRow(
                text = stringResource(R.string.settings_theme_dark),
                selected = currentSetting == ThemeSetting.DARK,
                onClick = { viewModel.updateThemeSetting(ThemeSetting.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeChoiceRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // El clic se maneja en la fila
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
