package com.holosphere.launcher.feature.widgets

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WidgetHostScreen() {
    val context = LocalContext.current
    val controller = remember { WidgetHostController(context) }
    var widgetId by remember { mutableStateOf<Int?>(null) }
    var widgetInfo by remember { mutableStateOf<android.appwidget.AppWidgetProviderInfo?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val id = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
            widgetId = id
            widgetInfo = controller.manager.getAppWidgetInfo(id)
        }
    }
    DisposableEffect(controller) {
        controller.start()
        onDispose { controller.stop() }
    }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Painel de widgets")
        Button(onClick = {
            val id = controller.allocateId()
            picker.launch(Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id))
        }) { Text("Adicionar widget") }
        val id = widgetId
        val info = widgetInfo
        if (id != null && info != null) {
            AndroidView(factory = { controller.createView(it, id, info) }, modifier = Modifier.padding(top = 16.dp))
        } else {
            Text("Os widgets externos aparecem aqui depois da autorização do sistema.")
        }
    }
}
