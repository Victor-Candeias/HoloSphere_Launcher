package com.holosphere.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore("launcher_settings")

data class LauncherSettings(val motionEnabled: Boolean = true, val labelsEnabled: Boolean = true, val darkTheme: Boolean = true)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val motion = booleanPreferencesKey("motion_enabled")
        val labels = booleanPreferencesKey("labels_enabled")
        val dark = booleanPreferencesKey("dark_theme")
    }

    val settings: Flow<LauncherSettings> = context.settingsDataStore.data.map { prefs ->
        LauncherSettings(prefs[Keys.motion] ?: true, prefs[Keys.labels] ?: true, prefs[Keys.dark] ?: true)
    }

    suspend fun setMotionEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.motion] = enabled }
    suspend fun setLabelsEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.labels] = enabled }
    suspend fun setDarkTheme(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.dark] = enabled }
}
