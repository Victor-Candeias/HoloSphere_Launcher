package com.holosphere.launcher.feature.home

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import com.holosphere.launcher.data.CalendarEvent
import com.holosphere.launcher.data.CalendarRepository
import java.text.DateFormat
import java.util.Date

@Composable
fun CalendarCard() {
    val context = LocalContext.current
    val repository = remember { CalendarRepository(context) }
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) }
    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    var denied by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
        denied = !granted
    }
    LaunchedEffect(hasPermission) {
        if (hasPermission) events = runCatching { repository.upcoming() }.getOrDefault(emptyList())
    }
    Column(Modifier.padding(top = 8.dp)) {
        Text("Agenda")
        when {
            !hasPermission -> {
                Text(if (denied) "Acesso ao calendário recusado." else "Permite acesso opcional aos próximos eventos.")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CALENDAR) }) { Text("Permitir calendário") }
            }
            events.isEmpty() -> Text("Sem próximos eventos")
            else -> events.take(2).forEach { event ->
                Text("${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(event.startMillis))}: ${event.title}")
            }
        }
    }
}
