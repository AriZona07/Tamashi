package com.oolestudio.tamashi.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.ui.tutorial.SpeechBubble
import com.oolestudio.tamashi.ui.tutorial.TamashiAvatar

@Composable
fun WelcomeMessageScreen(
    userName: String,
    tamashiName: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TamashiAvatar(tamashiName = tamashiName)
            Spacer(modifier = Modifier.height(16.dp))
            SpeechBubble(text = "¡Hola, $userName, yo seré tu guía para cumplir tus objetivos personales")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onConfirm) {
                Text("Comenzar")
            }
        }
    }
}
