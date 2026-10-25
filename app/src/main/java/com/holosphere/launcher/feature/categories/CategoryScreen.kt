package com.holosphere.launcher.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.CategoryRepository
import com.holosphere.launcher.domain.Category
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(onSelect: (String) -> Unit = {}) {
    val context = LocalContext.current
    val repository = remember(context) { CategoryRepository(context) }
    val categories = repository.categories.collectAsState(initial = emptyList()).value
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Constelações", color = Color.White)
        Row {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nova categoria") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                val cleanName = name.trim()
                if (cleanName.isNotEmpty()) {
                    scope.launch { repository.upsert(Category(cleanName.lowercase().replace(" ", "-"), cleanName, 0xFFB8C2E8)) }
                    name = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) { Text("Adicionar") }
        }
        LazyColumn {
            items(categories, key = { it.id }) { category ->
                Row(Modifier.padding(vertical = 3.dp)) {
                    Button(onClick = { onSelect(category.id) }, modifier = Modifier.weight(1f)) { Text(category.name) }
                    Button(onClick = { scope.launch { repository.delete(category.id) } }) { Text("Remover") }
                }
            }
        }
    }
}
