package com.holosphere.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.holosphere.launcher.domain.Category
import com.holosphere.launcher.domain.defaultCategories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class CategoryRepository(private val context: Context) {
    private val key = stringPreferencesKey("categories")
    val categories: Flow<List<Category>> = context.settingsDataStore.data.map { prefs -> decode(prefs[key]) }

    suspend fun upsert(category: Category) = context.settingsDataStore.edit { prefs ->
        prefs[key] = encode(decode(prefs[key]).filterNot { it.id == category.id } + category)
    }

    suspend fun delete(id: String) = context.settingsDataStore.edit { prefs ->
        prefs[key] = encode(decode(prefs[key]).filterNot { it.id == id })
    }

    private fun decode(raw: String?): List<Category> {
        if (raw.isNullOrBlank()) return defaultCategories
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                val apps = item.optJSONArray("apps") ?: JSONArray()
                Category(item.getString("id"), item.getString("name"), item.getLong("color"), (0 until apps.length()).map { apps.getString(it) }.toSet())
            }
        }.getOrDefault(defaultCategories)
    }

    private fun encode(categories: List<Category>): String = JSONArray().apply {
        categories.forEach { category ->
            put(JSONObject().put("id", category.id).put("name", category.name).put("color", category.color).put("apps", JSONArray(category.appPackageNames.toList())))
        }
    }.toString()
}
