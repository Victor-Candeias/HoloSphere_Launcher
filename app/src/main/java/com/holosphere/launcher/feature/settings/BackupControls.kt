package com.holosphere.launcher.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.BackupRepository
import com.holosphere.launcher.data.LauncherSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BackupControls(settings: LauncherSettings, favorites: Set<String>) {
    val context = LocalContext.current
    val repository = remember { BackupRepository(context) }
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var exportJson by remember { mutableStateOf<String?>(null) }
    val createDocument = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val json = exportJson ?: return@rememberLauncherForActivityResult
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            status = "Backup exportado."
        }
    }
    val openDocument = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: error("Ficheiro vazio")
                repository.restore(json)
            }.onSuccess { status = "Backup restaurado." }.onFailure { status = "Backup inválido: ${it.message ?: "erro desconhecido"}" }
        }
    }
    Column(Modifier.padding(top = 16.dp)) {
        Text("Dados")
        Row {
            Button(onClick = {
                exportJson = repository.export(settings, favorites)
                createDocument.launch("holosphere-backup.json")
            }) { Text("Exportar") }
            Button(onClick = { openDocument.launch(arrayOf("application/json", "text/plain")) }, modifier = Modifier.padding(start = 8.dp)) { Text("Importar") }
        }
        status?.let { Text(it) }
    }
}
