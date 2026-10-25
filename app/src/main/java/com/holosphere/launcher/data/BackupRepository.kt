package com.holosphere.launcher.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class BackupMetadata(val schemaVersion: Int, val createdAt: Long, val appVersion: String)
data class BackupPayload(val metadata: BackupMetadata, val settings: LauncherSettings, val favoritePackages: Set<String>)

class BackupRepository(private val context: Context) {
    fun export(settings: LauncherSettings, favoritePackages: Set<String>): String = JSONObject()
        .put("schemaVersion", SCHEMA_VERSION)
        .put("createdAt", System.currentTimeMillis())
        .put("appVersion", APP_VERSION)
        .put("settings", JSONObject()
            .put("motionEnabled", settings.motionEnabled)
            .put("labelsEnabled", settings.labelsEnabled)
            .put("darkTheme", settings.darkTheme)
            .put("sphereDensity", settings.sphereDensity)
            .put("sphereStyle", settings.sphereStyle)
            .put("backgroundStyle", settings.backgroundStyle)
            .put("primaryColor", settings.primaryColor)
            .put("particlesEnabled", settings.particlesEnabled)
            .put("blurEnabled", settings.blurEnabled)
            .put("twoDimensionalMode", settings.twoDimensionalMode)
            .put("reduceMotion", settings.reduceMotion)
            .put("largeText", settings.largeText)
            .put("highContrast", settings.highContrast)
            .put("performanceProfile", settings.performanceProfile))
        .put("favorites", JSONArray(favoritePackages.toList()))
        .toString()

    fun validate(json: String): BackupMetadata = parse(json).metadata

    suspend fun restore(json: String): BackupPayload {
        val payload = parse(json)
        SettingsRepository(context).restore(payload.settings, payload.favoritePackages)
        return payload
    }

    private fun parse(json: String): BackupPayload {
        require(json.toByteArray(Charsets.UTF_8).size <= MAX_BYTES) { "Backup demasiado grande" }
        val root = JSONObject(json)
        val metadata = BackupMetadata(root.getInt("schemaVersion"), root.getLong("createdAt"), root.getString("appVersion"))
        require(metadata.schemaVersion == SCHEMA_VERSION) { "Versão de backup não suportada" }
        val rawSettings = root.getJSONObject("settings")
        val favorites = root.getJSONArray("favorites")
        val packages = (0 until favorites.length()).map { favorites.getString(it) }.filter { it.length <= 255 }.toSet()
        return BackupPayload(metadata, LauncherSettings(
            motionEnabled = rawSettings.optBoolean("motionEnabled", true),
            labelsEnabled = rawSettings.optBoolean("labelsEnabled", true),
            darkTheme = rawSettings.optBoolean("darkTheme", true),
            sphereDensity = rawSettings.optDouble("sphereDensity", .5).toFloat().coerceIn(0f, 1f),
            sphereStyle = rawSettings.optString("sphereStyle", "sphere"),
            backgroundStyle = rawSettings.optString("backgroundStyle", "gradient"),
            primaryColor = rawSettings.optLong("primaryColor", 0xFF7DE2FF),
            particlesEnabled = rawSettings.optBoolean("particlesEnabled", false),
            blurEnabled = rawSettings.optBoolean("blurEnabled", false),
            twoDimensionalMode = rawSettings.optBoolean("twoDimensionalMode", false),
            reduceMotion = rawSettings.optBoolean("reduceMotion", false),
            largeText = rawSettings.optBoolean("largeText", false),
            highContrast = rawSettings.optBoolean("highContrast", false),
            performanceProfile = rawSettings.optString("performanceProfile", "balanced")
        ), packages)
    }

    companion object {
        const val SCHEMA_VERSION = 1
        const val APP_VERSION = "0.1.0"
        const val MAX_BYTES = 1_048_576
    }
}
