package com.holosphere.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class Weather(val temperatureC: Double, val weatherCode: Int, val fetchedAt: Long)

class WeatherRepository(private val context: Context) {
    private val cacheKey = stringPreferencesKey("weather_cache")

    suspend fun getCached(): Weather? = context.settingsDataStore.data.first()[cacheKey]?.let {
        runCatching { JSONObject(it).let { json -> Weather(json.getDouble("temperatureC"), json.getInt("weatherCode"), json.getLong("fetchedAt")) } }.getOrNull()
    }

    suspend fun fetch(latitude: Double, longitude: Double): Weather = withContext(Dispatchers.IO) {
        val json = JSONObject(URL("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code").readText())
        val current = json.getJSONObject("current")
        Weather(current.getDouble("temperature_2m"), current.getInt("weather_code"), System.currentTimeMillis()).also { weather ->
            context.settingsDataStore.edit { it[cacheKey] = JSONObject().put("temperatureC", weather.temperatureC).put("weatherCode", weather.weatherCode).put("fetchedAt", weather.fetchedAt).toString() }
        }
    }
}
