package com.holosphere.launcher.feature.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.AppRepository

@Composable
fun AppDrawerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val apps = remember { repository.load() }
    val query = remember { mutableStateOf("") }
    val filtered = apps.filter { it.label.contains(query.value, ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onBack) { Text("Voltar") }
        OutlinedTextField(query.value, { query.value = it }, label = { Text("Pesquisar aplicações") })
        LazyColumn {
            items(filtered, key = { it.packageName }) { app ->
                Button(onClick = { repository.launch(app) }, modifier = Modifier.padding(vertical = 2.dp)) { Text(app.label) }
            }
        }
    }
}
