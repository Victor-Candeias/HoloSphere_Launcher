package com.holosphere.launcher.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holosphere.launcher.data.Weather
import com.holosphere.launcher.data.WeatherRepository
import com.holosphere.launcher.data.isFresh
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

private const val LISBON_LATITUDE = 38.7223
private const val LISBON_LONGITUDE = -9.1393

@Composable
fun WeatherCard() {
    val repository = remember { WeatherRepository(LocalContext.current) }
    val scope = rememberCoroutineScope()
    var weather by remember { mutableStateOf<Weather?>(null) }
    var loading by remember { mutableStateOf(true) }
    var offline by remember { mutableStateOf(false) }

    suspend fun refresh() {
        loading = true
        weather = repository.getCached()
        runCatching { repository.fetch(LISBON_LATITUDE, LISBON_LONGITUDE) }
            .onSuccess { weather = it; offline = false }
            .onFailure { offline = true }
        loading = false
    }

    LaunchedEffect(Unit) { refresh() }
    Column(Modifier.padding(top = 8.dp)) {
        Text(weather?.let { "${it.temperatureC.toInt()}°C · ${weatherDescription(it.weatherCode)}" } ?: "Meteorologia indisponível")
        Text(if (loading) "A atualizar…" else if (offline) "Modo offline · cache local" else "Lisboa · ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(weather?.fetchedAt ?: 0))}")
        if (!loading && (weather?.isFresh() != true || offline)) Button(onClick = { scope.launch { refresh() } }) { Text("Atualizar") }
    }
}

private fun weatherDescription(code: Int): String = when (code) {
    0 -> "céu limpo"
    in 1..3 -> "parcialmente nublado"
    in 45..48 -> "nevoeiro"
    in 51..67 -> "chuva"
    in 71..77 -> "neve"
    in 80..82 -> "aguaceiros"
    else -> "trovoada"
}
