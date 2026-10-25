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
    val repository = remember { SettingsRepository(LocalContext.current) }
    val settings = repository.settings.collectAsState(initial = LauncherSettings()).value
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Acessibilidade")
        Text("Modo 2D / lista")
        Switch(settings.twoDimensionalMode, { scope.launch { repository.setTwoDimensionalMode(it) } })
        Text("Reduzir movimento")
        Switch(settings.reduceMotion, { scope.launch { repository.setReduceMotion(it) } })
        Text("Texto grande")
        Switch(settings.largeText, { scope.launch { repository.setLargeText(it) } })
        Text("Alto contraste")
        Switch(settings.highContrast, { scope.launch { repository.setHighContrast(it) } })
    }
}
