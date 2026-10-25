package com.holosphere.launcher.feature.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.AppRepository
import com.holosphere.launcher.data.SettingsRepository
import com.holosphere.launcher.domain.normalizeForSearch
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val settings = remember { SettingsRepository(context) }
    val scope = rememberCoroutineScope()
    val favorites = settings.favoritePackages.collectAsState(initial = emptySet()).value
    val apps = remember { repository.load() }
    val query = remember { mutableStateOf("") }
    val normalizedQuery = query.value.normalizeForSearch()
    val filtered = apps.filter { normalizedQuery.isBlank() || it.label.normalizeForSearch().contains(normalizedQuery) }
        .sortedWith(compareByDescending<com.holosphere.launcher.data.InstalledApp> { it.packageName in favorites }.thenBy { it.label.normalizeForSearch() })
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onBack) { Text("Voltar") }
        OutlinedTextField(query.value, { query.value = it }, label = { Text("Pesquisar aplicações") })
        Text("${filtered.size} aplicações")
        LazyColumn {
            items(filtered, key = { it.packageName }) { app ->
                Row(Modifier.padding(vertical = 2.dp)) {
                    Button(onClick = { repository.launch(app) }, modifier = Modifier.weight(1f)) { Text(app.label) }
                    Button(onClick = {
                        scope.launch { settings.setFavorite(app.packageName, app.packageName !in favorites) }
                    }) { Text(if (app.packageName in favorites) "★" else "☆") }
                }
            }
        }
        if (filtered.isEmpty()) Text("Nenhuma aplicação encontrada.")
    }
}
