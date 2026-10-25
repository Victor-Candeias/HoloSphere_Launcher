package com.holosphere.launcher.feature.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SphereEditorScreen() {
    val density = remember { mutableFloatStateOf(.5f) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Editor da esfera")
        Text("Densidade")
        Slider(density, { density.floatValue = it })
        Text("Estilo: Esfera")
    }
}
