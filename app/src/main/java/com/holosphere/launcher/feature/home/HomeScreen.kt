package com.holosphere.launcher.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.sphere.SphereCanvas
import com.holosphere.launcher.feature.apps.AppDrawerScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(factory: HomeViewModelFactory) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state = viewModel.uiState.collectAsState().value
    val selected = remember { mutableStateOf<String?>(null) }
    val drawer = remember { mutableStateOf(false) }
    if (drawer.value) {
        AppDrawerScreen { drawer.value = false }
        return
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF080B18)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(state.title, color = Color.White)
        Text(state.subtitle, color = Color(0xFFB8C2E8), modifier = Modifier.padding(top = 8.dp))
        LauncherSetupPrompt()
        Button(onClick = { drawer.value = true }, modifier = Modifier.padding(top = 12.dp)) { Text("Todas as aplicações") }
        SphereCanvas(Modifier.weight(1f).padding(16.dp), onNodeClick = { index ->
            val app = state.apps.getOrNull(index)
            selected.value = app?.label ?: "Ícone ${index + 1}"
            app?.let(viewModel::launch)
        })
        selected.value?.let { Text("A abrir: $it", color = Color(0xFF7DE2FF)) }
    }
}
