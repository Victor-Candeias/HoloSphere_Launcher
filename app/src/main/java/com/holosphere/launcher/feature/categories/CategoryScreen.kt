package com.holosphere.launcher.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.domain.defaultCategories

@Composable
fun CategoryScreen(onSelect: (String) -> Unit = {}) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Constelações", color = Color.White)
        defaultCategories.forEach { category ->
            Button(onClick = { onSelect(category.id) }) { Text(category.name) }
        }
    }
}
