package com.holosphere.launcher.feature.home

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.system.LauncherRole

@Composable
fun LauncherSetupPrompt() {
    val context = LocalContext.current
    var isDefault by remember { mutableStateOf(LauncherRole.isDefault(context)) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        isDefault = LauncherRole.isDefault(context)
    }
    LaunchedEffect(Unit) { isDefault = LauncherRole.isDefault(context) }
    if (!isDefault) {
        Column(Modifier.padding(top = 12.dp)) {
            Text("HoloSphere ainda não é o launcher predefinido.")
            Button(onClick = {
                launcher.launch(LauncherRole.requestIntent(context) ?: Intent(Settings.ACTION_HOME_SETTINGS))
            }, modifier = Modifier.padding(top = 6.dp)) {
                Text("Definir como launcher")
            }
        }
    }
}
