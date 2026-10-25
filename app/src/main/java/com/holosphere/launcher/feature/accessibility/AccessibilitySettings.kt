package com.holosphere.launcher.feature.accessibility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun AccessibilityScreen() {
    val context = LocalContext.current
    val repository = remember(context) { SettingsRepository(context) }
    val settings = repository.settings.collectAsState(initial = LauncherSettings()).value
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Acessibilidade")
        Text("Modo 2D / lista")
        Switch(checked = settings.twoDimensionalMode, onCheckedChange = { value -> scope.launch { repository.setTwoDimensionalMode(value) } })
        Text("Reduzir movimento")
        Switch(checked = settings.reduceMotion, onCheckedChange = { value -> scope.launch { repository.setReduceMotion(value) } })
        Text("Texto grande")
        Switch(checked = settings.largeText, onCheckedChange = { value -> scope.launch { repository.setLargeText(value) } })
        Text("Alto contraste")
        Switch(checked = settings.highContrast, onCheckedChange = { value -> scope.launch { repository.setHighContrast(value) } })
    }
}
