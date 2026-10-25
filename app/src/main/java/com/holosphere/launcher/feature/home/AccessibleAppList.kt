package com.holosphere.launcher.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.holosphere.launcher.domain.model.LauncherApp

@Composable
fun AccessibleAppList(apps: List<LauncherApp>, largeText: Boolean, onLaunch: (LauncherApp) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().semantics { contentDescription = "Lista acessível de aplicações" }) {
        items(apps, key = { it.packageName }) { app ->
            Button(onClick = { onLaunch(app) }) {
                Text(app.label, fontSize = if (largeText) 22.sp else 16.sp, fontWeight = if (largeText) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
