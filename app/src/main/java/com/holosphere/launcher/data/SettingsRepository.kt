package com.holosphere.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore("launcher_settings")

data class LauncherSettings(val motionEnabled: Boolean = true, val labelsEnabled: Boolean = true, val darkTheme: Boolean = true, val sphereDensity: Float = .5f, val sphereStyle: String = "sphere")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val motion = booleanPreferencesKey("motion_enabled")
        val labels = booleanPreferencesKey("labels_enabled")
        val dark = booleanPreferencesKey("dark_theme")
        val favorites = stringSetPreferencesKey("favorite_packages")
        val sphereDensity = floatPreferencesKey("sphere_density")
        val sphereStyle = androidx.datastore.preferences.core.stringPreferencesKey("sphere_style")
    }

    val settings: Flow<LauncherSettings> = context.settingsDataStore.data.map { prefs ->
        LauncherSettings(prefs[Keys.motion] ?: true, prefs[Keys.labels] ?: true, prefs[Keys.dark] ?: true, prefs[Keys.sphereDensity] ?: .5f, prefs[Keys.sphereStyle] ?: "sphere")
    }

    val favoritePackages: Flow<Set<String>> = context.settingsDataStore.data.map { it[Keys.favorites] ?: emptySet() }

    suspend fun setMotionEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.motion] = enabled }
    suspend fun setLabelsEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.labels] = enabled }
    suspend fun setDarkTheme(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.dark] = enabled }
    suspend fun setFavorite(packageName: String, favorite: Boolean) = context.settingsDataStore.edit { prefs ->
        val favorites = (prefs[Keys.favorites] ?: emptySet()).toMutableSet()
        if (favorite) favorites += packageName else favorites -= packageName
        prefs[Keys.favorites] = favorites
    }

    suspend fun setSphereDensity(value: Float) = context.settingsDataStore.edit { it[Keys.sphereDensity] = value }
    suspend fun setSphereStyle(value: String) = context.settingsDataStore.edit { it[Keys.sphereStyle] = value }
}
