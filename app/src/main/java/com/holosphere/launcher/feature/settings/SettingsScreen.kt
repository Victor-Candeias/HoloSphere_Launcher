package com.holosphere.launcher.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.LauncherSettings
import com.holosphere.launcher.data.SettingsRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val repository = remember { SettingsRepository(LocalContext.current) }
    val settings = repository.settings.collectAsState(initial = LauncherSettings()).value
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Aparência")
        Row { Text("Rótulos"); Switch(settings.labelsEnabled, { value -> scope.launch { repository.setLabelsEnabled(value) } }) }
        Row { Text("Partículas"); Switch(settings.particlesEnabled, { value -> scope.launch { repository.setParticlesEnabled(value) } }) }
        Row { Text("Blur"); Switch(settings.blurEnabled, { value -> scope.launch { repository.setBlurEnabled(value) } }) }
        Text("Fundo")
        Row {
            Button(onClick = { scope.launch { repository.setBackgroundStyle("solid") } }) { Text("Sólido") }
            Button(onClick = { scope.launch { repository.setBackgroundStyle("gradient") } }, modifier = Modifier.padding(start = 8.dp)) { Text("Gradiente") }
        }
        Text("Tema: ${if (settings.darkTheme) "escuro" else "claro"}")
        Button(onClick = { scope.launch { repository.setDarkTheme(!settings.darkTheme) } }) { Text("Alternar tema") }
    }
}
