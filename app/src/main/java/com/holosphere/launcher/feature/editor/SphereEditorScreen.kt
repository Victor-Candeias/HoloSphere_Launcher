package com.holosphere.launcher.feature.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SphereEditorScreen() {
    val context = LocalContext.current
    val repository = remember(context) { SettingsRepository(context) }
    val settings = repository.settings.collectAsState(initial = LauncherSettings()).value
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Editor da esfera")
        Text("Densidade: ${(settings.sphereDensity * 100).toInt()}%")
        Slider(value = settings.sphereDensity, onValueChange = { value -> scope.launch { repository.setSphereDensity(value) } })
        Text("Estilo: ${settings.sphereStyle}")
        listOf("sphere", "rings", "galaxy", "minimal", "constellations").forEach { style ->
            TextButton(onClick = { scope.launch { repository.setSphereStyle(style) } }) { Text(style) }
        }
    }
}
