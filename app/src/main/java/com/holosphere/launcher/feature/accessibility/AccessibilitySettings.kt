package com.holosphere.launcher.feature.accessibility

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

data class AccessibilitySettings(val twoDimensionalMode: Boolean = false, val reduceMotion: Boolean = false, val largeText: Boolean = false)

@Composable
fun AccessibilityScreen() {
    val twoD = remember { mutableStateOf(false) }
    val reduceMotion = remember { mutableStateOf(false) }
    val largeText = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Acessibilidade")
        Text("Modo 2D / lista")
        Switch(twoD.value, { twoD.value = it })
        Text("Reduzir movimento")
        Switch(reduceMotion.value, { reduceMotion.value = it })
        Text("Texto grande")
        Switch(largeText.value, { largeText.value = it })
    }
}
