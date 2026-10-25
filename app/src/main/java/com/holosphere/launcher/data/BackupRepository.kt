package com.holosphere.launcher.data

import android.content.Context
import org.json.JSONObject

data class BackupMetadata(val schemaVersion: Int, val createdAt: Long, val appVersion: String)

class BackupRepository(private val context: Context) {
    fun export(settings: LauncherSettings, favoritePackages: Set<String>): String = JSONObject()
        .put("schemaVersion", 1)
        .put("createdAt", System.currentTimeMillis())
        .put("appVersion", "0.1.0")
        .put("settings", JSONObject().put("motionEnabled", settings.motionEnabled).put("labelsEnabled", settings.labelsEnabled).put("darkTheme", settings.darkTheme))
        .put("favorites", favoritePackages.toList())
        .toString()

    fun validate(json: String): BackupMetadata {
        val root = JSONObject(json)
        require(root.getInt("schemaVersion") == 1) { "Versão de backup não suportada" }
        require(root.has("settings") && root.has("favorites")) { "Backup incompleto" }
        return BackupMetadata(root.getInt("schemaVersion"), root.getLong("createdAt"), root.getString("appVersion"))
    }
}
