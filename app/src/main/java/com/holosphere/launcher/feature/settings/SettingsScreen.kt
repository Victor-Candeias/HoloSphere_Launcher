package com.holosphere.launcher.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    val labels = remember { mutableStateOf(true) }
    val particles = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Aparência")
        Text("Rótulos")
        Switch(labels.value, { labels.value = it })
        Text("Partículas")
        Switch(particles.value, { particles.value = it })
    }
}
