package com.holosphere.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore("launcher_settings")

data class LauncherSettings(
    val motionEnabled: Boolean = true,
    val labelsEnabled: Boolean = true,
    val darkTheme: Boolean = true,
    val sphereDensity: Float = .5f,
    val sphereStyle: String = "sphere",
    val backgroundStyle: String = "gradient",
    val primaryColor: Long = 0xFF7DE2FF,
    val particlesEnabled: Boolean = false,
    val blurEnabled: Boolean = false,
    val twoDimensionalMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val largeText: Boolean = false,
    val highContrast: Boolean = false,
    val performanceProfile: String = "balanced"
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val motion = booleanPreferencesKey("motion_enabled")
        val labels = booleanPreferencesKey("labels_enabled")
        val dark = booleanPreferencesKey("dark_theme")
        val favorites = stringSetPreferencesKey("favorite_packages")
        val sphereDensity = floatPreferencesKey("sphere_density")
        val sphereStyle = androidx.datastore.preferences.core.stringPreferencesKey("sphere_style")
        val backgroundStyle = androidx.datastore.preferences.core.stringPreferencesKey("background_style")
        val primaryColor = longPreferencesKey("primary_color")
        val particles = booleanPreferencesKey("particles_enabled")
        val blur = booleanPreferencesKey("blur_enabled")
        val twoD = booleanPreferencesKey("two_dimensional_mode")
        val reduceMotion = booleanPreferencesKey("reduce_motion")
        val largeText = booleanPreferencesKey("large_text")
        val highContrast = booleanPreferencesKey("high_contrast")
        val performanceProfile = androidx.datastore.preferences.core.stringPreferencesKey("performance_profile")
    }

    val settings: Flow<LauncherSettings> = context.settingsDataStore.data.map { prefs ->
        LauncherSettings(
            prefs[Keys.motion] ?: true,
            prefs[Keys.labels] ?: true,
            prefs[Keys.dark] ?: true,
            prefs[Keys.sphereDensity] ?: .5f,
            prefs[Keys.sphereStyle] ?: "sphere",
            prefs[Keys.backgroundStyle] ?: "gradient",
            prefs[Keys.primaryColor] ?: 0xFF7DE2FF,
            prefs[Keys.particles] ?: false,
            prefs[Keys.blur] ?: false,
            prefs[Keys.twoD] ?: false,
            prefs[Keys.reduceMotion] ?: false,
            prefs[Keys.largeText] ?: false,
            prefs[Keys.highContrast] ?: false,
            prefs[Keys.performanceProfile] ?: "balanced"
        )
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
    suspend fun setBackgroundStyle(value: String) = context.settingsDataStore.edit { it[Keys.backgroundStyle] = value }
    suspend fun setPrimaryColor(value: Long) = context.settingsDataStore.edit { it[Keys.primaryColor] = value }
    suspend fun setParticlesEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.particles] = enabled }
    suspend fun setBlurEnabled(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.blur] = enabled }
    suspend fun setTwoDimensionalMode(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.twoD] = enabled }
    suspend fun setReduceMotion(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.reduceMotion] = enabled }
    suspend fun setLargeText(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.largeText] = enabled }
    suspend fun setHighContrast(enabled: Boolean) = context.settingsDataStore.edit { it[Keys.highContrast] = enabled }
    suspend fun setPerformanceProfile(value: String) = context.settingsDataStore.edit { it[Keys.performanceProfile] = value }
    suspend fun restore(settings: LauncherSettings, favorites: Set<String>) = context.settingsDataStore.edit { prefs ->
        prefs[Keys.motion] = settings.motionEnabled
        prefs[Keys.labels] = settings.labelsEnabled
        prefs[Keys.dark] = settings.darkTheme
        prefs[Keys.sphereDensity] = settings.sphereDensity
        prefs[Keys.sphereStyle] = settings.sphereStyle
        prefs[Keys.backgroundStyle] = settings.backgroundStyle
        prefs[Keys.primaryColor] = settings.primaryColor
        prefs[Keys.particles] = settings.particlesEnabled
        prefs[Keys.blur] = settings.blurEnabled
        prefs[Keys.twoD] = settings.twoDimensionalMode
        prefs[Keys.reduceMotion] = settings.reduceMotion
        prefs[Keys.largeText] = settings.largeText
        prefs[Keys.highContrast] = settings.highContrast
        prefs[Keys.performanceProfile] = settings.performanceProfile
        prefs[Keys.favorites] = favorites
    }
}
